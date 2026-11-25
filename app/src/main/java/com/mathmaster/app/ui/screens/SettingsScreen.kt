package com.mathmaster.app.ui.screens

@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mathmaster.app.data.preferences.SettingsState
import com.mathmaster.app.data.preferences.ThemeSetting
import com.mathmaster.app.domain.model.Difficulty
import com.mathmaster.app.ui.common.AppCard
import com.mathmaster.app.ui.common.AppTopBar
import com.mathmaster.app.ui.common.SectionTitle
import com.mathmaster.app.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    state: SettingsState,
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset progress?") },
            text = {
                Text("This permanently deletes all attempts, mistakes, topic mastery and table mastery from this device. Preferences are kept.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetProgress()
                    showResetDialog = false
                }) { Text("Reset") }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        AppTopBar("Settings", onBack = onBack)

        SectionTitle("Appearance")
        Spacer(modifier = Modifier.height(10.dp))
        AppCard {
            Text("Theme", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeSetting.entries.forEach { theme ->
                    OptionButton(
                        label = theme.name.lowercase().replaceFirstChar { it.uppercase() },
                        selected = state.theme == theme,
                        onClick = { viewModel.setTheme(theme) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        SectionTitle("Practice behaviour")
        Spacer(modifier = Modifier.height(10.dp))
        AppCard {
            ToggleRow("Auto check", "Answer is checked as soon as it matches.", state.autoCheck,
                viewModel::setAutoCheck)
            ToggleRow("Auto next", "Move to the next question after a correct answer.", state.autoNext,
                viewModel::setAutoNext)
            Spacer(modifier = Modifier.height(10.dp))
            Text("Auto next delay: ${state.autoNextDelayMs} ms", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = state.autoNextDelayMs.toFloat(),
                onValueChange = { viewModel.setAutoNextDelay((it / 100f).toLong() * 100L) },
                valueRange = 300f..2500f,
                steps = 21,
                modifier = Modifier.fillMaxWidth()
            )
            ToggleRow("Sound", "Short tone after each answer.", state.soundEnabled, viewModel::setSound)
            ToggleRow("Haptic feedback", "Light vibration after each answer.", state.hapticEnabled,
                viewModel::setHaptic)
        }

        Spacer(modifier = Modifier.height(18.dp))
        SectionTitle("Timer and goals")
        Spacer(modifier = Modifier.height(10.dp))
        AppCard {
            ToggleRow("Timer", "Add a timer to practice sessions and tests.", state.timerEnabled,
                viewModel::setTimerEnabled)
            StepperRow(
                title = "Time per question",
                value = "${state.timerSeconds} s",
                onMinus = { viewModel.setTimerSeconds(state.timerSeconds - 10) },
                onPlus = { viewModel.setTimerSeconds(state.timerSeconds + 10) }
            )
            StepperRow(
                title = "Daily practice target",
                value = "${state.dailyGoal} questions",
                onMinus = { viewModel.setDailyGoal(state.dailyGoal - 5) },
                onPlus = { viewModel.setDailyGoal(state.dailyGoal + 5) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        SectionTitle("Difficulty")
        Spacer(modifier = Modifier.height(10.dp))
        AppCard {
            Text("Difficulty controls the number range and complexity of generated questions.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Difficulty.entries.forEach { difficulty ->
                    OptionButton(
                        label = difficulty.name.lowercase().replaceFirstChar { it.uppercase() },
                        selected = state.difficulty == difficulty,
                        onClick = { viewModel.setDifficulty(difficulty) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        SectionTitle("Data")
        Spacer(modifier = Modifier.height(10.dp))
        AppCard {
            Text("All data stays on this device. No login or network connection is required.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                onClick = { showResetDialog = true },
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset practice progress", style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun StepperRow(title: String, value: String, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.titleMedium)
        TextButton(onClick = onMinus) { Text("-") }
        TextButton(onClick = onPlus) { Text("+") }
    }
}

@Composable
private fun OptionButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 10.dp), textAlign = TextAlign.Center)
    }
}
