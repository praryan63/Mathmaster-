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
import com.mathmaster.app.domain.model.PracticeMode
import com.mathmaster.app.domain.question.TopicCatalog
import com.mathmaster.app.ui.common.AppCard
import com.mathmaster.app.ui.common.AppTopBar
import com.mathmaster.app.ui.common.PrimaryButton
import com.mathmaster.app.ui.common.SecondaryButton
import com.mathmaster.app.ui.navigation.Routes

@Composable
fun IbpsScreen(
    onNavigate: (String) -> Unit,
    onPractice: (String, PracticeMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        AppTopBar("IBPS PO Aptitude")
        Text(
            "All 20 quantitative aptitude sections with unlimited practice and timed tests.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
            Text("Full syllabus test", style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text("Mixed questions from every IBPS PO topic.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton("Configure test", { onNavigate(Routes.quizSetup("mixed")) },
                modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(16.dp))
        TopicCatalog.ibps.forEach { topic ->
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(topic.name, style = MaterialTheme.typography.titleMedium)
                Text(topic.description, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PrimaryButton("Practice", { onPractice(topic.id, PracticeMode.PRACTICE) },
                        modifier = Modifier.weight(1f))
                    SecondaryButton("Test", { onNavigate(Routes.quizSetup(topic.id)) },
                        modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
