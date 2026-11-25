package com.mathmaster.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mathmaster.app.data.preferences.SettingsState
import com.mathmaster.app.domain.analytics.AnalyticsEngine
import com.mathmaster.app.domain.model.PracticeMode
import com.mathmaster.app.domain.question.TopicCatalog
import com.mathmaster.app.ui.common.AppCard
import com.mathmaster.app.ui.common.Chip
import com.mathmaster.app.ui.common.EmptyState
import com.mathmaster.app.ui.common.MathLogo
import com.mathmaster.app.ui.common.PrimaryButton
import com.mathmaster.app.ui.common.ProgressTrack
import com.mathmaster.app.ui.common.SectionTitle
import com.mathmaster.app.ui.common.StatTile
import com.mathmaster.app.ui.navigation.Routes
import com.mathmaster.app.util.DateFormatters
import com.mathmaster.app.viewmodel.HomeUiState
import java.util.Calendar

@Composable
fun HomeScreen(
    state: HomeUiState,
    settings: SettingsState,
    onNavigate: (String) -> Unit,
    onPractice: (String, PracticeMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            MathLogo(size = 68.dp)
            Spacer(modifier = Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Math Master", style = MaterialTheme.typography.headlineLarge)
                Text("Learn. Practise. Master.", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { onNavigate(Routes.SETTINGS) }) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
        Text(greeting, style = MaterialTheme.typography.headlineMedium)
        Text(
            "Your daily mathematics practice at a glance.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))
        AppCard {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Daily learning progress", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Chip("${state.todayAttempts}/${state.dailyGoal}")
            }
            Spacer(modifier = Modifier.height(12.dp))
            ProgressTrack(state.goalProgress)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                if (state.todayAttempts >= state.dailyGoal) "Daily target complete. Excellent work."
                else "${state.dailyGoal - state.todayAttempts} questions to reach today's target.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                label = "Total attempted",
                value = state.totalAttempts.toString(),
                caption = "${state.correctAttempts} correct answers",
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = "Accuracy",
                value = "${state.accuracy}%",
                caption = "All saved practice",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                label = "Practice streak",
                value = "${state.streak} days",
                caption = "Consecutive active days",
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = "Mistake notebook",
                value = state.mistakeCount.toString(),
                caption = "Questions to revisit",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle("Continue learning")
        Spacer(modifier = Modifier.height(10.dp))
        AppCard(onClick = { onPractice(settings.lastTopicId, PracticeMode.PRACTICE) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(TopicCatalog.nameOf(settings.lastTopicId), style = MaterialTheme.typography.titleMedium)
                    Text("Pick up where you left off", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(Icons.Filled.PlayArrow, contentDescription = "Continue")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle("Quick access")
        Spacer(modifier = Modifier.height(10.dp))
        val quickAccess = listOf(
            Triple("Basic Maths", Icons.Filled.Calculate, { onNavigate(Routes.LEARN) }),
            Triple("Tables 1-50", Icons.Filled.GridView, { onNavigate(Routes.TABLES) }),
            Triple("IBPS PO", Icons.Filled.Quiz, { onNavigate(Routes.IBPS) }),
            Triple("Speed Maths", Icons.Filled.Timer, { onNavigate(Routes.SPEED) }),
            Triple("Mistake Notebook", Icons.Filled.MenuBook, { onNavigate(Routes.MISTAKES) }),
            Triple("Tests", Icons.Filled.Star, { onNavigate(Routes.quizSetup()) })
        )
        repeat(2) { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                quickAccess.drop(row * 3).take(3).forEach { item ->
                    QuickCard(
                        title = item.first,
                        icon = item.second,
                        onClick = item.third,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))
        SectionTitle("Recent activity", actionText = "Analytics") { onNavigate(Routes.ANALYTICS) }
        Spacer(modifier = Modifier.height(10.dp))
        if (state.recent.isEmpty()) {
            AppCard {
                Text(
                    "No practice yet. Start with a topic above and your progress will appear here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                )
            }
        } else {
            state.recent.forEach { attempt ->
                AppCard(
                    onClick = { onPractice(attempt.topicId, PracticeMode.PRACTICE) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(attempt.topicName, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${DateFormatters.timeLabel(attempt.timestamp)} · ${DateFormatters.durationLabel(attempt.durationMs)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Chip(
                            if (attempt.isCorrect) "Correct" else "Review",
                            if (attempt.isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (state.weakTopics.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            SectionTitle("Focus areas", actionText = "Mistakes") { onNavigate(Routes.MISTAKES) }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                state.weakTopics.forEach { topic ->
                    Chip("${topic.topicName} ${AnalyticsEngine.accuracy(topic.correct, topic.attempts)}%")
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun QuickCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(onClick = onClick, modifier = modifier) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Start)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Open", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
