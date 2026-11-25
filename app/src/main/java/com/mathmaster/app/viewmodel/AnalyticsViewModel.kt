package com.mathmaster.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathmaster.app.data.preferences.SettingsRepository
import com.mathmaster.app.data.repository.PracticeRepository
import com.mathmaster.app.domain.analytics.AnalyticsEngine
import com.mathmaster.app.domain.analytics.DayPoint
import com.mathmaster.app.domain.analytics.PeriodPoint
import com.mathmaster.app.util.DateFormatters
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class TopicPerformance(
    val name: String,
    val attempts: Int,
    val accuracy: Int,
    val averageSeconds: Double
)

data class TablePerformance(val number: Int, val attempts: Int, val accuracy: Int)

data class AnalyticsUiState(
    val totalAttempts: Int = 0,
    val correct: Int = 0,
    val incorrect: Int = 0,
    val accuracy: Int = 0,
    val averageSeconds: Double = 0.0,
    val streak: Int = 0,
    val todayAttempts: Int = 0,
    val dailyGoal: Int = 20,
    val daily: List<DayPoint> = emptyList(),
    val weekly: List<PeriodPoint> = emptyList(),
    val monthly: List<PeriodPoint> = emptyList(),
    val strongTopics: List<TopicPerformance> = emptyList(),
    val weakTopics: List<TopicPerformance> = emptyList(),
    val weakTables: List<TablePerformance> = emptyList(),
    val masteredTables: Int = 0,
    val totalMistakes: Int = 0,
    val improvedMistakes: Int = 0
)

class AnalyticsViewModel(
    repository: PracticeRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val mistakeInfo = combine(
        repository.mistakes,
        repository.improvedMistakeCount
    ) { all, improved -> Pair(all, improved) }

    val uiState: StateFlow<AnalyticsUiState> = combine(
        repository.attempts,
        repository.topicStats,
        repository.tableMastery,
        mistakeInfo,
        settingsRepository.settingsFlow
    ) { attempts, topicStats, tables, mistakeData, settings ->
        val mistakes = mistakeData.first
        val improved = mistakeData.second
        val correct = attempts.count { it.isCorrect }
        val total = attempts.size
        val dayCounts = attempts.groupBy { it.day }.mapValues { (_, items) ->
            Pair(items.size, items.count { it.isCorrect })
        }
        val performances = topicStats.map { stat ->
            TopicPerformance(
                name = stat.topicName,
                attempts = stat.attempts,
                accuracy = AnalyticsEngine.accuracy(stat.correct, stat.attempts),
                averageSeconds = AnalyticsEngine.averageSeconds(stat.totalTimeMs, stat.attempts)
            )
        }
        val tablePerformances = tables.map { table ->
            TablePerformance(
                number = table.tableNumber,
                attempts = table.attempts,
                accuracy = AnalyticsEngine.accuracy(table.correct, table.attempts)
            )
        }
        AnalyticsUiState(
            totalAttempts = total,
            correct = correct,
            incorrect = total - correct,
            accuracy = AnalyticsEngine.accuracy(correct, total),
            averageSeconds = AnalyticsEngine.averageSeconds(attempts.sumOf { it.durationMs }, total),
            streak = AnalyticsEngine.streak(attempts.map { it.day }.toSet()),
            todayAttempts = attempts.count { it.day == DateFormatters.dayKey(0) },
            dailyGoal = settings.dailyGoal,
            daily = AnalyticsEngine.lastNDays(dayCounts, 7),
            weekly = AnalyticsEngine.lastNWeeks(dayCounts, 6),
            monthly = AnalyticsEngine.lastNMonths(dayCounts, 6),
            strongTopics = performances.filter { it.attempts >= 3 && it.accuracy >= 80 }
                .sortedByDescending { it.accuracy }.take(6),
            weakTopics = performances.filter { it.attempts >= 2 && it.accuracy < 70 }
                .sortedBy { it.accuracy }.take(6),
            weakTables = tablePerformances.filter { it.attempts >= 3 && it.accuracy < 75 }
                .sortedBy { it.accuracy }.take(6),
            masteredTables = tablePerformances.count { it.attempts >= 5 && it.accuracy >= 90 },
            totalMistakes = mistakes.size,
            improvedMistakes = improved
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), AnalyticsUiState())
}
