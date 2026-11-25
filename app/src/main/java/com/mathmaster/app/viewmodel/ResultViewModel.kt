package com.mathmaster.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathmaster.app.data.local.entity.AttemptEntity
import com.mathmaster.app.data.repository.PracticeRepository
import com.mathmaster.app.domain.analytics.AnalyticsEngine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ResultUiState(
    val attempts: List<AttemptEntity> = emptyList(),
    val total: Int = 0,
    val correct: Int = 0,
    val accuracy: Int = 0,
    val averageSeconds: Double = 0.0,
    val totalSeconds: Long = 0L
)

class ResultViewModel(sessionId: String, repository: PracticeRepository) : ViewModel() {

    val uiState: StateFlow<ResultUiState> = repository.sessionAttempts(sessionId).map { attempts ->
        ResultUiState(
            attempts = attempts,
            total = attempts.size,
            correct = attempts.count { it.isCorrect },
            accuracy = AnalyticsEngine.accuracy(attempts.count { it.isCorrect }, attempts.size),
            averageSeconds = AnalyticsEngine.averageSeconds(attempts.sumOf { it.durationMs }, attempts.size),
            totalSeconds = attempts.sumOf { it.durationMs } / 1000L
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), ResultUiState())
}
