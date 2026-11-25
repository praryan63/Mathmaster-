package com.mathmaster.app.viewmodel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathmaster.app.data.preferences.SettingsRepository
import com.mathmaster.app.data.preferences.SettingsState
import com.mathmaster.app.data.repository.PracticeRepository
import com.mathmaster.app.domain.model.Difficulty
import com.mathmaster.app.domain.model.PracticeMode
import com.mathmaster.app.domain.model.Question
import com.mathmaster.app.domain.model.Topic
import com.mathmaster.app.domain.question.QuestionBank
import com.mathmaster.app.domain.question.TopicCatalog
import com.mathmaster.app.domain.validation.AnswerValidator
import com.mathmaster.app.util.FeedbackManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class FeedbackKind { NONE, CORRECT, WRONG, TIME_UP }

data class PracticeUiState(
    val topic: Topic,
    val question: Question? = null,
    val input: String = "",
    val feedback: FeedbackKind = FeedbackKind.NONE,
    val feedbackMessage: String = "",
    val showAnswer: Boolean = false,
    val questionNumber: Int = 0,
    val totalQuestions: Int = 0,
    val sessionCorrect: Int = 0,
    val sessionAttempts: Int = 0,
    val elapsedMs: Long = 0L,
    val remainingMs: Long? = null,
    val finished: Boolean = false,
    val sessionId: String = "",
    val mode: PracticeMode = PracticeMode.PRACTICE,
    val difficulty: Difficulty = Difficulty.EASY,
    val timerEnabled: Boolean = false,
    val autoNextDelayMs: Long = 900L
)

class PracticeViewModel(
    private val topicId: String,
    private val mode: PracticeMode,
    private val questionCount: Int,
    private val timerRequested: Boolean,
    private val repository: PracticeRepository,
    private val settingsRepository: SettingsRepository,
    private val questionBank: QuestionBank,
    private val feedbackManager: FeedbackManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PracticeUiState(
            topic = TopicCatalog.find(topicId),
            totalQuestions = if (mode == PracticeMode.QUIZ) questionCount else 0,
            mode = mode,
            sessionId = UUID.randomUUID().toString().take(12)
        )
    )
    val uiState: StateFlow<PracticeUiState> = _uiState

    private val settings = settingsRepository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsState()
    )

    private val queue = mutableListOf<Question>()
    private val usedSignatures = mutableSetOf<String>()
    private var weakTables: Set<Int> = emptySet()
    private var mistakeQuestions: List<Question> = emptyList()
    private var mistakeIndex = 0
    private var currentQuestion: Question? = null
    private var questionStartedAt = SystemClock.elapsedRealtime()
    private var lastTickAt = SystemClock.elapsedRealtime()
    private var answered = false
    private var autoNextJob: Job? = null
    private var timerRemainingMs: Long? = null

    init {
        viewModelScope.launch {
            val currentSettings = settingsRepository.settingsFlow.first()
            weakTables = repository.weakTableNumbers()
            usedSignatures.addAll(repository.recentSignatures(60))
            if (mode == PracticeMode.MISTAKE) {
                mistakeQuestions = repository.mistakeQuestions()
                    .filter { topicId == "all" || it.topicId == topicId }
            }
            if (mode == PracticeMode.QUIZ) {
                queue.addAll(
                    questionBank.generate(
                        topicId = topicId,
                        difficulty = currentSettings.difficulty,
                        count = questionCount,
                        avoid = usedSignatures,
                        weakTables = weakTables
                    )
                )
            }
            val timerOn = timerRequested || currentSettings.timerEnabled
            if (timerOn) {
                val duration = currentSettings.timerSeconds * 1000L
                timerRemainingMs = if (mode == PracticeMode.QUIZ) duration * questionCount.coerceAtLeast(1) else duration
            }
            _uiState.update {
                it.copy(
                    difficulty = currentSettings.difficulty,
                    timerEnabled = timerOn,
                    remainingMs = timerRemainingMs,
                    autoNextDelayMs = currentSettings.autoNextDelayMs
                )
            }
            loadNextQuestion()
        }
    }

    private suspend fun loadNextQuestion() {
        if (_uiState.value.finished) return
        val next: Question? = when (mode) {
            PracticeMode.QUIZ -> if (queue.isEmpty()) null else queue.removeAt(0)
            PracticeMode.MISTAKE -> {
                if (mistakeIndex >= mistakeQuestions.size) null else mistakeQuestions[mistakeIndex++]
            }
            else -> questionBank.generate(topicId, _uiState.value.difficulty, 1, usedSignatures, weakTables).firstOrNull()
        }
        if (next == null) {
            finishSession()
            return
        }
        usedSignatures.add(next.signature)
        currentQuestion = next
        answered = false
        questionStartedAt = SystemClock.elapsedRealtime()
        lastTickAt = questionStartedAt
        if (mode != PracticeMode.QUIZ && _uiState.value.timerEnabled) {
            timerRemainingMs = settings.value.timerSeconds * 1000L
        }
        _uiState.update { state ->
            state.copy(
                question = next,
                input = "",
                feedback = FeedbackKind.NONE,
                feedbackMessage = "",
                showAnswer = false,
                questionNumber = state.questionNumber + 1,
                elapsedMs = 0L,
                remainingMs = timerRemainingMs
            )
        }
    }

    fun onInputChange(value: String) {
        if (answered || _uiState.value.finished) return
        _uiState.update { it.copy(input = value) }
        val question = currentQuestion ?: return
        if (settings.value.autoCheck && value.isNotBlank() && AnswerValidator.isCorrect(question, value)) {
            submit(automatic = true)
        }
    }

    fun submit(automatic: Boolean = false) {
        val question = currentQuestion ?: return
        if (answered || _uiState.value.finished) return
        val answer = _uiState.value.input.trim()
        if (!automatic && answer.isEmpty()) {
            _uiState.update {
                it.copy(feedback = FeedbackKind.WRONG, feedbackMessage = "Enter an answer first.")
            }
            return
        }
        val correct = AnswerValidator.isCorrect(question, answer)
        commitAnswer(question, answer, correct, FeedbackKind.CORRECT, FeedbackKind.WRONG, automatic)
    }

    private fun commitAnswer(
        question: Question,
        answer: String,
        correct: Boolean,
        correctFeedback: FeedbackKind,
        wrongFeedback: FeedbackKind,
        automatic: Boolean
    ) {
        answered = true
        autoNextJob?.cancel()
        val duration = (SystemClock.elapsedRealtime() - questionStartedAt).coerceAtLeast(0L)
        val sessionId = _uiState.value.sessionId
        viewModelScope.launch {
            repository.recordAttempt(
                question = question,
                userAnswer = answer,
                isCorrect = correct,
                durationMs = duration,
                mode = mode,
                sessionId = sessionId
            )
        }
        feedbackManager.play(correct, settings.value.soundEnabled)
        _uiState.update { state ->
            state.copy(
                feedback = if (correct) correctFeedback else wrongFeedback,
                feedbackMessage = if (correct) {
                    "Correct. Well done."
                } else if (wrongFeedback == FeedbackKind.TIME_UP) {
                    "Time is up. The correct answer is ${question.answer}."
                } else {
                    "Not quite. The correct answer is ${question.answer}."
                },
                showAnswer = !correct,
                sessionCorrect = state.sessionCorrect + if (correct) 1 else 0,
                sessionAttempts = state.sessionAttempts + 1
            )
        }
        if (correct && settings.value.autoNext && mode != PracticeMode.QUIZ) {
            scheduleNext(settings.value.autoNextDelayMs)
        } else if (correct && mode == PracticeMode.QUIZ && settings.value.autoNext) {
            scheduleNext(settings.value.autoNextDelayMs)
        }
    }

    private fun scheduleNext(delayMs: Long) {
        autoNextJob?.cancel()
        autoNextJob = viewModelScope.launch {
            delay(delayMs.coerceAtLeast(150L))
            nextQuestion()
        }
    }

    fun nextQuestion() {
        autoNextJob?.cancel()
        viewModelScope.launch { loadNextQuestion() }
    }

    fun retry() {
        if (!answered) return
        autoNextJob?.cancel()
        answered = false
        questionStartedAt = SystemClock.elapsedRealtime()
        lastTickAt = questionStartedAt
        if (_uiState.value.timerEnabled && mode != PracticeMode.QUIZ) {
            timerRemainingMs = settings.value.timerSeconds * 1000L
        }
        _uiState.update {
            it.copy(
                input = "",
                feedback = FeedbackKind.NONE,
                feedbackMessage = "",
                showAnswer = false,
                elapsedMs = 0L,
                remainingMs = timerRemainingMs
            )
        }
    }

    fun revealAnswer() {
        _uiState.update { it.copy(showAnswer = true) }
    }

    fun skip() {
        val question = currentQuestion ?: return
        if (answered) {
            nextQuestion()
            return
        }
        commitAnswer(question, "", false, FeedbackKind.CORRECT, FeedbackKind.WRONG, automatic = true)
        scheduleNext(700L)
    }

    fun onTick() {
        if (_uiState.value.finished) return
        val now = SystemClock.elapsedRealtime()
        val delta = now - lastTickAt
        lastTickAt = now
        _uiState.update { it.copy(elapsedMs = now - questionStartedAt) }
        val remaining = timerRemainingMs ?: return
        val next = remaining - delta
        if (next > 0L) {
            timerRemainingMs = next
            _uiState.update { it.copy(remainingMs = next) }
        } else {
            timerRemainingMs = 0L
            _uiState.update { it.copy(remainingMs = 0L) }
            handleTimeout()
        }
    }

    private fun handleTimeout() {
        val question = currentQuestion ?: return
        if (answered) return
        if (mode == PracticeMode.QUIZ) {
            commitAnswer(question, "", false, FeedbackKind.CORRECT, FeedbackKind.TIME_UP, automatic = true)
            viewModelScope.launch {
                delay(1000L)
                finishSession()
            }
            return
        }
        commitAnswer(question, "", false, FeedbackKind.CORRECT, FeedbackKind.TIME_UP, automatic = true)
        scheduleNext(1200L)
    }

    private fun finishSession() {
        autoNextJob?.cancel()
        _uiState.update { it.copy(finished = true, remainingMs = null) }
    }

    fun restart() {
        _uiState.update {
            PracticeUiState(
                topic = it.topic,
                totalQuestions = if (mode == PracticeMode.QUIZ) questionCount else 0,
                mode = mode,
                sessionId = UUID.randomUUID().toString().take(12),
                difficulty = it.difficulty,
                timerEnabled = it.timerEnabled,
                autoNextDelayMs = it.autoNextDelayMs
            )
        }
        queue.clear()
        usedSignatures.clear()
        mistakeIndex = 0
        currentQuestion = null
        answered = false
        timerRemainingMs = if (_uiState.value.timerEnabled) {
            if (mode == PracticeMode.QUIZ) settings.value.timerSeconds * 1000L * questionCount.coerceAtLeast(1)
            else settings.value.timerSeconds * 1000L
        } else null
        viewModelScope.launch {
            usedSignatures.addAll(repository.recentSignatures(60))
            weakTables = repository.weakTableNumbers()
            if (mode == PracticeMode.MISTAKE) mistakeQuestions = repository.mistakeQuestions()
            if (mode == PracticeMode.QUIZ) {
                queue.addAll(
                    questionBank.generate(topicId, _uiState.value.difficulty, questionCount, usedSignatures, weakTables)
                )
            }
            loadNextQuestion()
        }
    }

    override fun onCleared() {
        autoNextJob?.cancel()
        super.onCleared()
    }
}
