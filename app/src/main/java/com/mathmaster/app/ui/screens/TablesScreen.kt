package com.mathmaster.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mathmaster.app.domain.model.PracticeMode
import com.mathmaster.app.domain.question.TopicCatalog
import com.mathmaster.app.ui.common.AppCard
import com.mathmaster.app.ui.common.AppTopBar
import com.mathmaster.app.ui.common.Chip
import com.mathmaster.app.ui.common.PrimaryButton
import com.mathmaster.app.ui.common.SectionTitle
import com.mathmaster.app.ui.navigation.Routes
import com.mathmaster.app.viewmodel.TableUi

@Composable
fun TablesScreen(
    tables: List<TableUi>,
    onNavigate: (String) -> Unit,
    onPractice: (String, PracticeMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        AppTopBar("Multiplication Tables")
        Text(
            "Tables 1 to 50, each with answers from ×1 to ×20. Weak tables are marked for extra practice.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("Choose a table")
        Spacer(modifier = Modifier.height(10.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 78.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(440.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tables, key = { it.number }) { table ->
                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = if (table.weak) MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    onClick = { onNavigate(Routes.tableDetail(table.number)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("${table.number}", style = MaterialTheme.typography.titleLarge)
                        Text("${table.accuracy}%", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (table.weak) {
                            Text("Weak", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        SectionTitle("Practice modes")
        Spacer(modifier = Modifier.height(10.dp))
        TopicCatalog.tablePracticeModes.forEach { mode ->
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(mode.name, style = MaterialTheme.typography.titleMedium)
                        Text(mode.description, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (mode.id == "tables_weak") {
                        Chip("Adaptive")
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                PrimaryButton("Start practice", { onPractice(mode.id, PracticeMode.PRACTICE) },
                    modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
