package com.mathmaster.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathmaster.app.data.local.entity.MistakeEntity
import com.mathmaster.app.data.repository.PracticeRepository
import com.mathmaster.app.domain.question.TopicCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TopicFilter(val id: String, val label: String, val count: Int)
data class MistakesUiState(
    val mistakes: List<MistakeEntity> = emptyList(),
    val filters: List<TopicFilter> = emptyList(),
    val selectedTopic: String = "all",
    val total: Int = 0,
    val improved: Int = 0
)

class MistakesViewModel(private val repository: PracticeRepository) : ViewModel() {

    private val selectedTopic = MutableStateFlow("all")

    val uiState: StateFlow<MistakesUiState> = combine(
        repository.mistakes,
        repository.improvedMistakeCount,
        selectedTopic
    ) { all, improved, selected ->
        val filters = mutableListOf(TopicFilter("all", "All topics", all.size))
        all.groupBy { it.topicId }.forEach { (topicId, items) ->
            filters.add(TopicFilter(topicId, TopicCatalog.nameOf(topicId), items.size))
        }
        val visible = if (selected == "all") all else all.filter { it.topicId == selected }
        MistakesUiState(
            mistakes = visible,
            filters = filters.sortedBy { it.label },
            selectedTopic = selected,
            total = all.size,
            improved = improved
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), MistakesUiState())

    fun selectTopic(topicId: String) {
        selectedTopic.value = topicId
    }

    fun delete(signature: String) {
        viewModelScope.launch { repository.deleteMistake(signature) }
    }
}
