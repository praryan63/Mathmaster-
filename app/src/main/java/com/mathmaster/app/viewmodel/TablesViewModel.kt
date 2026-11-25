package com.mathmaster.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathmaster.app.data.repository.PracticeRepository
import com.mathmaster.app.domain.analytics.AnalyticsEngine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class TableUi(
    val number: Int,
    val attempts: Int,
    val accuracy: Int,
    val weak: Boolean
)

class TablesViewModel(repository: PracticeRepository) : ViewModel() {

    val uiState: StateFlow<List<TableUi>> = repository.tableMastery.map { mastery ->
        val byNumber = mastery.associateBy { it.tableNumber }
        (1..50).map { number ->
            val stat = byNumber[number]
            val attempts = stat?.attempts ?: 0
            val accuracy = AnalyticsEngine.accuracy(stat?.correct ?: 0, attempts)
            TableUi(
                number = number,
                attempts = attempts,
                accuracy = accuracy,
                weak = attempts >= 3 && accuracy < 70
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
}
