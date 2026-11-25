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
import com.mathmaster.app.ui.navigation.Routes
import com.mathmaster.app.viewmodel.TableDetailUiState

@Composable
fun TableDetailScreen(
    state: TableDetailUiState,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    onPractice: (String, PracticeMode) -> Unit
) {
    var answersVisible by rememberSaveable { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        AppTopBar("Table of ${state.number}", onBack = onBack)
        AppCard {
            Text("${state.number} × 1 to ${state.number} × 20", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip("Attempts ${state.attempts}")
                Chip("Accuracy ${state.accuracy}%")
            }
            Spacer(modifier = Modifier.height(14.dp))
            PrimaryButton("Practice this table", { onPractice("table_${state.number}", PracticeMode.PRACTICE) },
                modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SecondaryButton("Memorise", { onPractice("table_${state.number}", PracticeMode.PRACTICE) },
                    modifier = Modifier.weight(1f))
                SecondaryButton("Test", { onNavigate(Routes.quizSetup("table_${state.number}")) },
                    modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Learn and memorise", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            SecondaryButton(
                if (answersVisible) "Hide answers" else "Show answers",
                { answersVisible = !answersVisible }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        AppCard {
            state.rows.forEach { (multiplier, result) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${state.number} × $multiplier", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        if (answersVisible) "= $result" else "= ?",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (answersVisible) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
