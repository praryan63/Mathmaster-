package com.mathmaster.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mathmaster.app.domain.model.PracticeMode
import com.mathmaster.app.ui.common.AppCard
import com.mathmaster.app.ui.common.AppTopBar
import com.mathmaster.app.ui.common.Chip
import com.mathmaster.app.ui.common.PrimaryButton
import com.mathmaster.app.ui.common.SecondaryButton
import com.mathmaster.app.viewmodel.SpeedTopicUi

@Composable
fun SpeedScreen(
    topics: List<SpeedTopicUi>,
    onBack: () -> Unit,
    onPractice: (String, PracticeMode) -> Unit
) {
    var expandedTopic by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        AppTopBar("Speed Calculation", onBack = onBack)
        AppCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
            Text("Train your mind", style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text("Each challenge includes an optional timer, instant answers and unlimited questions.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton("Start mental maths challenge", {
                onPractice("speed_mental", PracticeMode.SPEED)
            }, modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(16.dp))
        topics.forEach { item ->
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.topic.name, style = MaterialTheme.typography.titleMedium)
                        Text("${item.attempts} attempts · ${item.accuracy}% accuracy",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Chip("Timed")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PrimaryButton("Start", { onPractice(item.topic.id, PracticeMode.SPEED) },
                        modifier = Modifier.weight(1f))
                    SecondaryButton(
                        if (expandedTopic == item.topic.id) "Hide tips" else "Tips",
                        { expandedTopic = if (expandedTopic == item.topic.id) "" else item.topic.id },
                        modifier = Modifier.weight(0.7f)
                    )
                }
                if (expandedTopic == item.topic.id) {
                    Spacer(modifier = Modifier.height(12.dp))
                    item.tips.forEach { tip ->
                        Text("• $tip", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
