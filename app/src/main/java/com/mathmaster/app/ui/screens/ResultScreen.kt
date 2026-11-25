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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mathmaster.app.ui.common.AppCard
import com.mathmaster.app.ui.common.AppTopBar
import com.mathmaster.app.ui.common.Chip
import com.mathmaster.app.ui.common.PrimaryButton
import com.mathmaster.app.ui.common.SecondaryButton
import com.mathmaster.app.util.DateFormatters
import com.mathmaster.app.viewmodel.ResultUiState

@Composable
fun ResultScreen(
    state: ResultUiState,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onRetry: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        AppTopBar("Test result", onBack = onBack)
        AppCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
            Text("${state.accuracy}% accuracy", style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(6.dp))
            Text("${state.correct} correct out of ${state.total} questions",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Average ${String.format("%.1f", state.averageSeconds)}s per question",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text("Total time ${DateFormatters.durationLabel(state.totalSeconds * 1000L)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton("Retry test", {
                    state.attempts.firstOrNull()?.let { onRetry(it.topicId) }
                }, modifier = Modifier.weight(1f))
                SecondaryButton("Home", onHome, modifier = Modifier.weight(0.7f))
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text("Answer review", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(10.dp))
        if (state.attempts.isEmpty()) {
            AppCard {
                Text("No answers were recorded in this session.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        state.attempts.forEachIndexed { index, attempt ->
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Question ${index + 1}", style = MaterialTheme.typography.labelLarge)
                    Chip(
                        if (attempt.isCorrect) "Correct" else "Incorrect",
                        if (attempt.isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(attempt.prompt, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Your answer: ${attempt.userAnswer.ifBlank { "no answer" }}",
                    style = MaterialTheme.typography.bodyMedium)
                Text("Correct answer: ${attempt.correctAnswer}",
                    style = MaterialTheme.typography.bodyMedium)
                Text("Time: ${DateFormatters.durationLabel(attempt.durationMs)} · ${attempt.topicName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Solution: ${attempt.explanation}", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
