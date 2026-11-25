package com.mathmaster.app.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BarChart(
    values: List<Int>,
    labels: List<String>,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    chartHeight: Int = 140
) {
    val maxValue = values.maxOrNull()?.coerceAtLeast(1) ?: 1
    Column(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxWidth().height(chartHeight.dp)) {
            val count = values.size.coerceAtLeast(1)
            val slot = size.width / count.toFloat()
            val barWidth = (slot * 0.48f).coerceAtMost(28.dp.toPx())
            val usableHeight = size.height * 0.86f
            values.forEachIndexed { index, value ->
                val barHeight = (value.toFloat() / maxValue.toFloat()) * usableHeight
                val x = slot * index.toFloat() + (slot - barWidth) / 2f
                val radius = minOf(8.dp.toPx(), barHeight / 2f)
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, size.height - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(radius, radius)
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MetricBar(
    label: String,
    value: String,
    progress: Float,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(text = value, style = MaterialTheme.typography.labelLarge, color = color)
        }
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
