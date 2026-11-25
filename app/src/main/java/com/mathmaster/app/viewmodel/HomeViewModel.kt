package com.mathmaster.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathmaster.app.data.local.dao.TopicStat
import com.mathmaster.app.data.local.entity.AttemptEntity
import com.mathmaster.app.data.preferences.SettingsRepository
import com.mathmaster.app.data.repository.PracticeRepository
import com.mathmaster.app.domain.analytics.AnalyticsEngine
import com.mathmaster.app.util.DateFormatters
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val totalAttempts: Int = 0,
    val correctAttempts: Int = 0,
    val accuracy: Int = 0,
    val streak: Int = 0,
    val todayAttempts: Int = 0,
    val dailyGoal: Int = 20,
    val goalProgress: Float = 0f,
    val recent: List<AttemptEntity> = emptyList(),
    val weakTopics: List<TopicStat> = emptyList(),
    val lastTopicId: String = "addition",
    val mistakeCount: Int = 0
)

class HomeViewModel(
    repository: PracticeRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.attempts,
        repository.topicStats,
        repository.mistakes,
        settingsRepository.settingsFlow
    ) { attempts, topicStats, mistakes, settings ->
        val today = DateFormatters.dayKey(0)
        val todayCount = attempts.count { it.day == today }
        val correct = attempts.count { it.isCorrect }
        val weak = topicStats
            .filter { it.attempts >= 3 && AnalyticsEngine.accuracy(it.correct, it.attempts) < 70 }
            .sortedBy { AnalyticsEngine.accuracy(it.correct, it.attempts) }
            .take(3)
        HomeUiState(
            totalAttempts = attempts.size,
            correctAttempts = correct,
            accuracy = AnalyticsEngine.accuracy(correct, attempts.size),
            streak = AnalyticsEngine.streak(attempts.map { it.day }.toSet()),
            todayAttempts = todayCount,
            dailyGoal = settings.dailyGoal,
            goalProgress = todayCount.toFloat() / settings.dailyGoal.coerceAtLeast(1).toFloat(),
            recent = attempts.take(5),
            weakTopics = weak,
            lastTopicId = settings.lastTopicId,
            mistakeCount = mistakes.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), HomeUiState())
}
