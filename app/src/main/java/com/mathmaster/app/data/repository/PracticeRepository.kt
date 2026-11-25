package com.mathmaster.app.data.repository

import com.mathmaster.app.data.local.MathDatabase
import com.mathmaster.app.data.local.dao.AttemptDao
import com.mathmaster.app.data.local.dao.MasteryDao
import com.mathmaster.app.data.local.dao.MistakeDao
import com.mathmaster.app.data.local.dao.TopicStat
import com.mathmaster.app.data.local.entity.AttemptEntity
import com.mathmaster.app.data.local.entity.MistakeEntity
import com.mathmaster.app.data.local.entity.TableMasteryEntity
import com.mathmaster.app.data.local.entity.TopicMasteryEntity
import com.mathmaster.app.domain.model.Difficulty
import com.mathmaster.app.domain.model.PracticeMode
import com.mathmaster.app.domain.model.Question
import com.mathmaster.app.domain.question.TopicCatalog
import com.mathmaster.app.util.DateFormatters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PracticeRepository(database: MathDatabase) {

    private val attemptDao: AttemptDao = database.attemptDao()
    private val mistakeDao: MistakeDao = database.mistakeDao()
    private val masteryDao: MasteryDao = database.masteryDao()

    val attempts: Flow<List<AttemptEntity>> = attemptDao.observeAll()
    val mistakes: Flow<List<MistakeEntity>> = mistakeDao.observeAll()
    val topicStats: Flow<List<TopicStat>> = attemptDao.topicStats()
    val dailyStats = attemptDao.dailyStats()
    val tableMastery: Flow<List<TableMasteryEntity>> = masteryDao.observeTables()
    val totalAttempts: Flow<Int> = attemptDao.count()
    val correctAttempts: Flow<Int> = attemptDao.correctCount()
    val totalDuration: Flow<Long> = attemptDao.totalDuration()
    val mistakeCount: Flow<Int> = mistakeDao.count()
    val improvedMistakeCount: Flow<Int> = mistakeDao.improvedCount()

    suspend fun recordAttempt(
        question: Question,
        userAnswer: String,
        isCorrect: Boolean,
        durationMs: Long,
        mode: PracticeMode,
        sessionId: String
    ) {
        val now = System.currentTimeMillis()
        attemptDao.insert(
            AttemptEntity(
                sessionId = sessionId,
                topicId = question.topicId,
                topicName = question.topicName,
                prompt = question.prompt,
                questionSignature = question.signature,
                correctAnswer = question.answer,
                explanation = question.explanation,
                userAnswer = userAnswer,
                isCorrect = isCorrect,
                durationMs = durationMs.coerceAtLeast(0L),
                difficulty = question.difficulty.name,
                mode = mode.name,
                day = DateFormatters.dayKey(),
                timestamp = now
            )
        )
        updateMistakes(question, userAnswer, isCorrect, now)
        updateTopicMastery(question, isCorrect, durationMs, now)
        TopicCatalog.tableNumber(question.topicId)?.let { number ->
            updateTableMastery(number, isCorrect, now)
        }
    }

    private suspend fun updateMistakes(question: Question, userAnswer: String, isCorrect: Boolean, now: Long) {
        val existing = mistakeDao.find(question.signature)
        if (!isCorrect) {
            val mistake = if (existing == null) {
                MistakeEntity(
                    signature = question.signature,
                    topicId = question.topicId,
                    topicName = question.topicName,
                    prompt = question.prompt,
                    correctAnswer = question.answer,
                    explanation = question.explanation,
                    lastWrongAnswer = userAnswer,
                    wrongCount = 1,
                    correctCount = 0,
                    improved = false,
                    createdAt = now,
                    lastPracticedAt = now
                )
            } else {
                existing.copy(
                    lastWrongAnswer = userAnswer,
                    wrongCount = existing.wrongCount + 1,
                    improved = false,
                    lastPracticedAt = now
                )
            }
            mistakeDao.upsert(mistake)
        } else if (existing != null) {
            val correctCount = existing.correctCount + 1
            mistakeDao.upsert(
                existing.copy(
                    correctCount = correctCount,
                    improved = correctCount >= 2 && correctCount >= existing.wrongCount,
                    lastPracticedAt = now
                )
            )
        }
    }

    private suspend fun updateTopicMastery(question: Question, isCorrect: Boolean, durationMs: Long, now: Long) {
        val existing = masteryDao.topic(question.topicId)
        val mastery = if (existing == null) {
            TopicMasteryEntity(
                topicId = question.topicId,
                topicName = question.topicName,
                attempts = 1,
                correct = if (isCorrect) 1 else 0,
                totalTimeMs = durationMs.coerceAtLeast(0L),
                lastPracticedAt = now
            )
        } else {
            existing.copy(
                attempts = existing.attempts + 1,
                correct = existing.correct + if (isCorrect) 1 else 0,
                totalTimeMs = existing.totalTimeMs + durationMs.coerceAtLeast(0L),
                lastPracticedAt = now
            )
        }
        masteryDao.upsertTopic(mastery)
    }

    private suspend fun updateTableMastery(number: Int, isCorrect: Boolean, now: Long) {
        val existing = masteryDao.table(number)
        val mastery = if (existing == null) {
            TableMasteryEntity(number, 1, if (isCorrect) 1 else 0, now)
        } else {
            existing.copy(
                attempts = existing.attempts + 1,
                correct = existing.correct + if (isCorrect) 1 else 0,
                lastPracticedAt = now
            )
        }
        masteryDao.upsertTable(mastery)
    }

    suspend fun recentSignatures(limit: Int = 60): Set<String> = attemptDao.recentSignatures(limit).toSet()

    suspend fun weakTableNumbers(): Set<Int> = masteryDao.observeTables().first()
        .filter { it.attempts >= 3 && it.correct.toDouble() / it.attempts < 0.7 }
        .map { it.tableNumber }
        .toSet()

    suspend fun mistakeQuestions(): List<Question> = mistakeDao.allOnce().map { mistake ->
        Question(
            signature = mistake.signature,
            topicId = mistake.topicId,
            topicName = mistake.topicName,
            prompt = mistake.prompt,
            answer = mistake.correctAnswer,
            explanation = mistake.explanation.ifBlank { "The correct answer is ${mistake.correctAnswer}." },
            difficulty = Difficulty.MEDIUM,
            hint = "This question was previously answered incorrectly.",
            tags = listOf("mistake", mistake.topicId)
        )
    }

    suspend fun recentAttempts(limit: Int = 5): List<AttemptEntity> = attemptDao.recent(limit)

    suspend fun attemptsForSession(sessionId: String): List<AttemptEntity> {
        return attemptDao.observeSession(sessionId).first()
    }

    fun sessionAttempts(sessionId: String): Flow<List<AttemptEntity>> = attemptDao.observeSession(sessionId)

    suspend fun deleteMistake(signature: String) = mistakeDao.delete(signature)

    suspend fun clearProgress() {
        attemptDao.clear()
        mistakeDao.clear()
        masteryDao.clearTopics()
        masteryDao.clearTables()
    }
}
