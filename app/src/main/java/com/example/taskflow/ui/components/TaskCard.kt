package com.example.taskflow.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
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
fun TaskCard(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        task.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (task.isCompleted) Muted else MaterialTheme.colorScheme.onSurface
                    )
                    Text(task.category, style = MaterialTheme.typography.bodyMedium, color = Muted)
                }
                TextButton(onClick = onDelete) { Text("✕", color = Muted) }
            }
            HorizontalDivider(color = SoftBorder)
            Row(verticalAlignment = Alignment.CenterVertically) {
                PriorityBadge(task.priority)
                Spacer(modifier = Modifier.weight(1f))
                Text(if (task.isCompleted) "Completada" else "Pendiente", color = if (task.isCompleted) Success else Muted, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: TaskPriority) {
    val label = when (priority) {
        TaskPriority.HIGH -> "Prioridad alta"
        TaskPriority.MEDIUM -> "Prioridad media"
        TaskPriority.LOW -> "Prioridad baja"
    }
    val tint = when (priority) {
        TaskPriority.HIGH -> Color(0xFFB91C1C)
        TaskPriority.MEDIUM -> Color(0xFFB45309)
        TaskPriority.LOW -> Color(0xFF047857)
    }
    val background = when (priority) {
        TaskPriority.HIGH -> Color(0xFFFEE2E2)
        TaskPriority.MEDIUM -> Color(0xFFFEF3C7)
        TaskPriority.LOW -> Color(0xFFD1FAE5)
    }
    Box(modifier = Modifier.background(background, RoundedCornerShape(40.dp)).padding(horizontal = 10.dp, vertical = 6.dp)) {
        Text(label, color = tint, style = MaterialTheme.typography.labelSmall)
    }
}
