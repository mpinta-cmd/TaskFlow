package com.example.taskflow.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.taskflow.model.Task
import com.example.taskflow.model.TaskFilter
import com.example.taskflow.model.TaskPriority
import com.example.taskflow.ui.theme.Muted
import com.example.taskflow.ui.theme.SoftBorder
import com.example.taskflow.ui.theme.Success
import com.example.taskflow.ui.theme.TaskFlowPalette


@Composable
fun StatisticsSection(total: Int, pending: Int, completed: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        StatisticCard("TOTAL", total, StatIcon.TOTAL, Modifier.weight(1f))
        StatisticCard("PENDIENTES", pending, StatIcon.PENDING, Modifier.weight(1f))
        StatisticCard("HECHAS", completed, StatIcon.DONE, Modifier.weight(1f))
    }
}

private enum class StatIcon { TOTAL, PENDING, DONE }

@Composable
private fun StatisticCard(
    label: String,
    count: Int,
    icon: StatIcon,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val accent = when (icon) {
        StatIcon.TOTAL -> primary
        StatIcon.PENDING -> Color(0xFFB77725)
        StatIcon.DONE -> Color(0xFF138568)
    }
    val iconBackground = when (icon) {
        StatIcon.TOTAL -> MaterialTheme.colorScheme.primaryContainer
        StatIcon.PENDING -> Color(0xFFFFF1DA)
        StatIcon.DONE -> Color(0xFFDDF6EB)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier.size(52.dp).background(iconBackground, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                StatisticIllustration(icon = icon, tint = accent)
            }
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                color = Muted,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun StatisticIllustration(icon: StatIcon, tint: Color) {
    // Iconos vectoriales dibujados con Canvas: no necesitan librerías externas.
    Canvas(modifier = Modifier.size(30.dp)) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.085f
        when (icon) {
            StatIcon.TOTAL -> {
                val tile = w * 0.30f
                val gap = w * 0.12f
                for (row in 0..1) for (col in 0..1) {
                    drawRoundRect(
                        color = tint,
                        topLeft = Offset(w * 0.14f + col * (tile + gap), h * 0.14f + row * (tile + gap)),
                        size = Size(tile, tile),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.065f)
                    )
                }
            }
            StatIcon.PENDING -> {
                drawCircle(tint, radius = w * 0.34f, style = Stroke(width = stroke))
                drawLine(tint, Offset(w * 0.5f, h * 0.5f), Offset(w * 0.5f, h * 0.28f),
                    strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(tint, Offset(w * 0.5f, h * 0.5f), Offset(w * 0.67f, h * 0.58f),
                    strokeWidth = stroke, cap = StrokeCap.Round)
            }
            StatIcon.DONE -> {
                drawCircle(tint, radius = w * 0.34f, style = Stroke(width = stroke))
                drawLine(tint, Offset(w * 0.31f, h * 0.51f), Offset(w * 0.45f, h * 0.65f),
                    strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(tint, Offset(w * 0.45f, h * 0.65f), Offset(w * 0.72f, h * 0.36f),
                    strokeWidth = stroke, cap = StrokeCap.Round)
            }
        }
    }
}
