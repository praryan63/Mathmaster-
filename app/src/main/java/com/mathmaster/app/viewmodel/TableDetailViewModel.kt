package com.mathmaster.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathmaster.app.data.repository.PracticeRepository
import com.mathmaster.app.domain.analytics.AnalyticsEngine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class TableDetailUiState(
    val number: Int = 1,
    val attempts: Int = 0,
    val correct: Int = 0,
    val accuracy: Int = 0,
    val rows: List<Pair<Int, Int>> = (1..20).map { it to it }
)

class TableDetailViewModel(number: Int, repository: PracticeRepository) : ViewModel() {

    val uiState: StateFlow<TableDetailUiState> = combine(
        repository.tableMastery,
        repository.attempts
    ) { mastery, attempts ->
        val stat = mastery.firstOrNull { it.tableNumber == number }
        val topicAttempts = attempts.count { it.topicId == "table_$number" }
        val topicCorrect = attempts.count { it.topicId == "table_$number" && it.isCorrect }
        TableDetailUiState(
            number = number,
            attempts = stat?.attempts ?: topicAttempts,
            correct = stat?.correct ?: topicCorrect,
            accuracy = AnalyticsEngine.accuracy(stat?.correct ?: topicCorrect, stat?.attempts ?: topicAttempts),
            rows = (1..20).map { multiplier -> multiplier to number * multiplier }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        TableDetailUiState(number = number)
    )
}
