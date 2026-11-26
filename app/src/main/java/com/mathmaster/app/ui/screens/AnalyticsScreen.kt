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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mathmaster.app.domain.analytics.PeriodPoint
import com.mathmaster.app.ui.common.AppCard
import com.mathmaster.app.ui.common.AppTopBar
import com.mathmaster.app.ui.common.BarChart
import com.mathmaster.app.ui.common.EmptyState
import com.mathmaster.app.ui.common.MetricBar
import com.mathmaster.app.ui.common.SectionTitle
import com.mathmaster.app.ui.common.StatTile
import com.mathmaster.app.viewmodel.AnalyticsUiState

@Composable
fun AnalyticsScreen(state: AnalyticsUiState, onBack: () -> Unit) {
    var period by rememberSaveable { mutableStateOf("Daily") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        AppTopBar("Analytics", onBack = onBack)
        if (state.totalAttempts == 0) {
            EmptyState(
                title = "No data yet",
                message = "Analytics are calculated from your saved answers. Complete some practice to see your progress."
            )
            return@Column
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile("Attempted", state.totalAttempts.toString(), "All time", modifier = Modifier.weight(1f))
            StatTile("Accuracy", "${state.accuracy}%", "${state.correct} correct", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile("Average time", "${String.format("%.1f", state.averageSeconds)}s", "Per question",
                modifier = Modifier.weight(1f))
            StatTile("Streak", "${state.streak} days", "Today ${state.todayAttempts}/${state.dailyGoal}",
                modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(22.dp))
        SectionTitle("Progress")
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Daily", "Weekly", "Monthly").forEach { item ->
                Surface(
                    onClick = { period = item },
                    shape = MaterialTheme.shapes.small,
                    color = if (period == item) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(item, style = MaterialTheme.typography.labelLarge,
                        color = if (period == item) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center)
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        AppCard {
            val chartData: List<Pair<String, Int>> = when (period) {
                "Weekly" -> state.weekly.map { it.label to it.attempted }
                "Monthly" -> state.monthly.map { it.label to it.attempted }
                else -> state.daily.map { it.label to it.attempted }
            }
            BarChart(values = chartData.map { it.second }, labels = chartData.map { it.first })
            Spacer(modifier = Modifier.height(8.dp))
            Text("Questions attempted", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.height(20.dp))
        SectionTitle("Strong and weak topics")
        Spacer(modifier = Modifier.height(10.dp))
        AppCard {
            Text("Strong topics", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if (state.strongTopics.isEmpty()) {
                Text("Keep practising to build strong topics.", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                state.strongTopics.take(4).forEach {
                    MetricBar(it.name, "${it.accuracy}%", it.accuracy / 100f)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Topics to improve", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if (state.weakTopics.isEmpty()) {
                Text("No weak topics detected yet.", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                state.weakTopics.take(4).forEach {
                    MetricBar(it.name, "${it.accuracy}%", it.accuracy / 100f, MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        SectionTitle("Table mastery")
        Spacer(modifier = Modifier.height(10.dp))
        AppCard {
            Text("${state.masteredTables} of 50 tables mastered", style = MaterialTheme.typography.titleMedium)
            if (state.weakTables.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                state.weakTables.forEach {
                    MetricBar("Table ${it.number}", "${it.accuracy}%", it.accuracy / 100f,
                        MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                Spacer(modifier = Modifier.height(6.dp))
                Text("No weak tables detected yet.", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        AppCard {
            Text("Mistake improvement", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("${state.totalMistakes} mistakes saved · ${state.improvedMistakes} improved",
                style = MaterialTheme.typography.bodyLarge)
            val improvement = if (state.totalMistakes == 0) 0f
            else state.improvedMistakes.toFloat() / state.totalMistakes.toFloat()
            MetricBar("Improved mistakes", "${(improvement * 100).toInt()}%", improvement,
                MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}
