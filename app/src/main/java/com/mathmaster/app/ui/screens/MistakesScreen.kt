package com.mathmaster.app.ui.screens

@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mathmaster.app.domain.model.PracticeMode
import com.mathmaster.app.ui.common.AppCard
import com.mathmaster.app.ui.common.AppTopBar
import com.mathmaster.app.ui.common.Chip
import com.mathmaster.app.ui.common.EmptyState
import com.mathmaster.app.ui.common.PrimaryButton
import com.mathmaster.app.viewmodel.MistakesUiState
import com.mathmaster.app.viewmodel.MistakesViewModel

@Composable
fun MistakesScreen(
    state: MistakesUiState,
    viewModel: MistakesViewModel,
    onBack: () -> Unit,
    onPractice: (String, PracticeMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        AppTopBar("Mistake Notebook", onBack = onBack)
        AppCard {
            Text("${state.total} saved mistakes", style = MaterialTheme.typography.headlineMedium)
            Text("${state.improved} questions marked improved after consistent correct answers.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(14.dp))
            PrimaryButton("Practice all mistakes", {
                onPractice("all", PracticeMode.MISTAKE)
            }, modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.filters.forEach { filter ->
                val selected = filter.id == state.selectedTopic
                Surface(
                    onClick = { viewModel.selectTopic(filter.id) },
                    shape = MaterialTheme.shapes.small,
                    color = if (selected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        "${filter.label} (${filter.count})",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (state.mistakes.isEmpty()) {
            EmptyState(
                title = if (state.total == 0) "No mistakes yet" else "No mistakes in this topic",
                message = "Wrong answers are saved here automatically with the correct solution."
            )
        } else {
            if (state.selectedTopic != "all") {
                PrimaryButton("Practice this topic", {
                    onPractice(state.selectedTopic, PracticeMode.MISTAKE)
                }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
            }
            state.mistakes.forEach { mistake ->
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.Top) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(mistake.topicName, style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(mistake.prompt, style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Your answer: ${mistake.lastWrongAnswer.ifBlank { "no answer" }}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error)
                            Text("Correct answer: ${mistake.correctAnswer}",
                                style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(mistake.explanation, style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Chip("Wrong ${mistake.wrongCount}x")
                                Chip("Correct ${mistake.correctCount}x")
                                if (mistake.improved) Chip("Improved") else Chip("Needs practice")
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Practice similar questions",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable {
                                    onPractice(mistake.topicId, PracticeMode.PRACTICE)
                                })
                        }
                        IconButton(onClick = { viewModel.delete(mistake.signature) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Remove mistake")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
