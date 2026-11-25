package com.mathmaster.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathmaster.app.data.repository.PracticeRepository
import com.mathmaster.app.domain.analytics.AnalyticsEngine
import com.mathmaster.app.domain.model.Topic
import com.mathmaster.app.domain.question.TopicCatalog
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SpeedTopicUi(
    val topic: Topic,
    val attempts: Int,
    val accuracy: Int,
    val tips: List<String>
)

class SpeedViewModel(repository: PracticeRepository) : ViewModel() {

    val uiState: StateFlow<List<SpeedTopicUi>> = repository.topicStats.map { stats ->
        val byId = stats.associateBy { it.topicId }
        TopicCatalog.speed.map { topic ->
            val stat = byId[topic.id]
            SpeedTopicUi(
                topic = topic,
                attempts = stat?.attempts ?: 0,
                accuracy = AnalyticsEngine.accuracy(stat?.correct ?: 0, stat?.attempts ?: 0),
                tips = tipsFor(topic.id)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    companion object {
        fun tipsFor(topicId: String): List<String> = when (topicId) {
            "speed_add" -> listOf("Round to the nearest ten, add, then adjust.", "Split numbers into tens and units.")
            "speed_sub" -> listOf("Add up from the smaller number to reach the larger one.", "Use complements of 10.")
            "speed_mul" -> listOf("Break one number into tens and units.", "Multiplying by 25 is the same as dividing by 4 and multiplying by 100.")
            "speed_div" -> listOf("Estimate the quotient first.", "Remove common factors before dividing.")
            "speed_percentage" -> listOf("10% means divide by 10.", "5% is half of 10%; 25% is one fourth.")
            "speed_squares" -> listOf("Use (a + b)² = a² + 2ab + b².", "Learn squares up to 30 by heart.")
            "speed_cubes" -> listOf("Memorise cubes up to 12 first.", "Cube means square times the number.")
            "speed_approximation" -> listOf("Round each number before calculating.", "Keep track of whether you rounded up or down.")
            "speed_mental" -> listOf("Compute left to right in small steps.", "Keep running totals in the tens place.")
            else -> listOf("Practise regularly to improve speed.")
        }
    }
}
