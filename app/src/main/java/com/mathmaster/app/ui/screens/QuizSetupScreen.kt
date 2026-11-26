@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mathmaster.app.domain.model.PracticeMode
import com.mathmaster.app.domain.question.TopicCatalog
import com.mathmaster.app.ui.common.AppCard
import com.mathmaster.app.ui.common.AppTopBar
import com.mathmaster.app.ui.common.Chip
import com.mathmaster.app.ui.common.PrimaryButton
import com.mathmaster.app.ui.common.SecondaryButton
import com.mathmaster.app.ui.common.SectionTitle
import com.mathmaster.app.viewmodel.QuizSetupUiState
import com.mathmaster.app.viewmodel.QuizSetupViewModel

@Composable
fun QuizSetupScreen(
    state: QuizSetupUiState,
    viewModel: QuizSetupViewModel,
    onBack: () -> Unit,
    onStart: (String, Int, Boolean) -> Unit
) {
    var category by rememberSaveable { mutableStateOf("All") }
    val topics = when (category) {
        "Basic" -> listOf(TopicCatalog.mixed) + TopicCatalog.basic
        "Tables" -> listOf(TopicCatalog.mixed) + TopicCatalog.tablePracticeModes
        "IBPS" -> listOf(TopicCatalog.mixed) + TopicCatalog.ibps
        else -> listOf(TopicCatalog.mixed) + TopicCatalog.basic + TopicCatalog.ibps
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        AppTopBar("Set up a test", onBack = onBack)
        AppCard {
            Text("Question count", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                SecondaryButton("-", { viewModel.stepCount(-5) }, modifier = Modifier.weight(0.4f))
                Text("${state.count}", style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                SecondaryButton("+", { viewModel.stepCount(5) }, modifier = Modifier.weight(0.4f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(10, 20, 30, 50).forEach { count ->
                    Surface(
                        onClick = { viewModel.setCount(count) },
                        shape = MaterialTheme.shapes.small,
                        color = if (state.count == count) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("$count", style = MaterialTheme.typography.labelLarge,
                            color = if (state.count == count) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Timer", style = MaterialTheme.typography.titleMedium)
                    Text("${state.timerSeconds} seconds per question", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = state.timerEnabled, onCheckedChange = viewModel::setTimer)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("Difficulty follows your Settings selection: ${state.difficulty.name.lowercase()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(18.dp))
        SectionTitle("Choose syllabus")
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Basic", "Tables", "IBPS").forEach { item ->
                Surface(
                    onClick = { category = item },
                    shape = MaterialTheme.shapes.small,
                    color = if (category == item) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(item, style = MaterialTheme.typography.labelLarge,
                        color = if (category == item) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        topics.forEach { topic ->
            val selected = topic.id == state.topicId
            Surface(
                onClick = { viewModel.selectTopic(topic.id) },
                shape = MaterialTheme.shapes.medium,
                color = if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(topic.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    if (selected) Chip("Selected") else Chip(topic.category.name)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton("Start test", {
            onStart(state.topicId, state.count, state.timerEnabled)
        }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(20.dp))
    }
}
