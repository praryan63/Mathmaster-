package com.mathmaster.app.ui.screens

import androidx.compose.foundation.HapticFeedbackType
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mathmaster.app.data.preferences.SettingsState
import com.mathmaster.app.domain.analytics.AnalyticsEngine
import com.mathmaster.app.ui.common.AnswerField
import com.mathmaster.app.ui.common.AppCard
import com.mathmaster.app.ui.common.AppTopBar
import com.mathmaster.app.ui.common.Chip
import com.mathmaster.app.ui.common.LinearProgressIndicatorCompat
import com.mathmaster.app.ui.common.PrimaryButton
import com.mathmaster.app.ui.common.SecondaryButton
import com.mathmaster.app.util.DateFormatters
import com.mathmaster.app.viewmodel.FeedbackKind
import com.mathmaster.app.viewmodel.PracticeUiState
import com.mathmaster.app.viewmodel.PracticeViewModel
import kotlinx.coroutines.delay

@Composable
fun PracticeScreen(
    state: PracticeUiState,
    settings: SettingsState,
    viewModel: PracticeViewModel,
    onBack: () -> Unit,
    onQuizFinished: (String) -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        while (true) {
            delay(200L)
            viewModel.onTick()
        }
    }

    LaunchedEffect(state.feedback) {
        if (state.feedback != FeedbackKind.NONE && settings.hapticEnabled) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    LaunchedEffect(state.finished) {
        if (state.finished && state.mode.name == "QUIZ") {
            onQuizFinished(state.sessionId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        AppTopBar(
            title = state.topic.name,
            onBack = onBack,
            actions = {
                Chip(state.difficulty.name.lowercase().replaceFirstChar { it.uppercase() })
            }
        )

        if (state.finished) {
            PracticeSummary(state = state, onRetry = viewModel::restart, onBack = onBack)
            return@Column
        }

        val question = state.question
        if (question == null) {
            Spacer(modifier = Modifier.height(80.dp))
            Text("Preparing questions...", style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            return@Column
        }

        val progress = if (state.totalQuestions > 0) {
            state.questionNumber.toFloat() / state.totalQuestions.toFloat()
        } else {
            (state.questionNumber % 10).toFloat() / 10f
        }
        LinearProgressIndicatorCompat(progress = progress, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Question ${state.questionNumber}${if (state.totalQuestions > 0) " of ${state.totalQuestions}" else ""}",
                style = MaterialTheme.typography.labelLarge)
            if (state.remainingMs != null) {
                Text(DateFormatters.clockLabel(state.remainingMs!!),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (state.remainingMs!! < 10_000L) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary)
            } else {
                Text(DateFormatters.durationLabel(state.elapsedMs), style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        AppCard {
            Text("Solve this", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))
            Text(question.prompt, style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth())
            if (question.hint.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Hint: ${question.hint}", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        AnswerField(
            value = state.input,
            onValueChange = viewModel::onInputChange,
            onSubmit = { viewModel.submit() },
            enabled = state.feedback == FeedbackKind.NONE
        )

        if (state.feedback != FeedbackKind.NONE) {
            Spacer(modifier = Modifier.height(12.dp))
            val color = if (state.feedback == FeedbackKind.CORRECT)
                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            AppCard(containerColor = color.copy(alpha = 0.12f)) {
                Text(
                    state.feedbackMessage,
                    style = MaterialTheme.typography.titleMedium,
                    color = color
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text("Correct answer: ${question.answer}", style = MaterialTheme.typography.bodyLarge)
                Text(question.explanation, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else if (state.showAnswer) {
            Spacer(modifier = Modifier.height(12.dp))
            AppCard {
                Text("Correct answer: ${question.answer}", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (state.feedback == FeedbackKind.NONE) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                PrimaryButton("Check answer", { viewModel.submit() }, modifier = Modifier.weight(1f))
                SecondaryButton("Skip", viewModel::skip, modifier = Modifier.weight(0.6f))
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                PrimaryButton("Next", viewModel::nextQuestion, modifier = Modifier.weight(1f))
                SecondaryButton("Retry", viewModel::retry, modifier = Modifier.weight(0.7f))
            }
            if (state.feedback != FeedbackKind.CORRECT && !state.showAnswer) {
                Spacer(modifier = Modifier.height(8.dp))
                SecondaryButton("Show answer", viewModel::revealAnswer, modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PracticeSummary(
    state: PracticeUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Spacer(modifier = Modifier.height(48.dp))
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text("Session complete", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("${state.topic.name} · ${state.mode.name.lowercase()}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Attempted", style = MaterialTheme.typography.bodyLarge)
            Text("${state.sessionAttempts}", style = MaterialTheme.typography.titleMedium)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Correct", style = MaterialTheme.typography.bodyLarge)
            Text("${state.sessionCorrect}", style = MaterialTheme.typography.titleMedium)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Accuracy", style = MaterialTheme.typography.bodyLarge)
            Text("${AnalyticsEngine.accuracy(state.sessionCorrect, state.sessionAttempts)}%",
                style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton("Practice again", onRetry, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        SecondaryButton("Back", onBack, modifier = Modifier.fillMaxWidth())
    }
}
