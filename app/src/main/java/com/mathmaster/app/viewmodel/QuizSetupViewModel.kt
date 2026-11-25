package com.mathmaster.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathmaster.app.data.preferences.SettingsRepository
import com.mathmaster.app.domain.model.Difficulty
import com.mathmaster.app.domain.question.TopicCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizSetupUiState(
    val topicId: String = "mixed",
    val count: Int = 10,
    val timerEnabled: Boolean = false,
    val timerSeconds: Int = 60,
    val difficulty: Difficulty = Difficulty.EASY
)

class QuizSetupViewModel(
    initialTopicId: String,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        QuizSetupUiState(
            topicId = if (TopicCatalog.find(initialTopicId).id == "mixed" && initialTopicId != "mixed") {
                initialTopicId
            } else initialTopicId
        )
    )
    val uiState: StateFlow<QuizSetupUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = settingsRepository.settingsFlow.first()
            _uiState.update {
                it.copy(
                    timerEnabled = settings.timerEnabled,
                    timerSeconds = settings.timerSeconds,
                    difficulty = settings.difficulty
                )
            }
        }
    }

    fun selectTopic(topicId: String) = _uiState.update { it.copy(topicId = topicId) }

    fun setCount(count: Int) = _uiState.update { it.copy(count = count.coerceIn(5, 100)) }

    fun stepCount(delta: Int) = setCount(_uiState.value.count + delta)

    fun setTimer(enabled: Boolean) = _uiState.update { it.copy(timerEnabled = enabled) }

    fun setDifficulty(difficulty: Difficulty) = _uiState.update { it.copy(difficulty = difficulty) }

    fun availableTopics() = listOf(TopicCatalog.mixed) + TopicCatalog.basic + TopicCatalog.ibps
}
