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
fun TaskHeader(
    palette: TaskFlowPalette,
    onPaletteChange: (TaskFlowPalette) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text("MI ESPACIO DE TRABAJO", color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text("Hoy es un buen día", style = MaterialTheme.typography.headlineLarge)
            Text("Organiza tus pendientes y mantén el ritmo.", color = Muted,
                style = MaterialTheme.typography.bodyMedium)
        }
        // Este botón sí funciona: alterna entre las dos paletas.
        // No cambia ni modifica las tareas de ejemplo.
        IconButton(onClick = {
            onPaletteChange(
                if (palette == TaskFlowPalette.OCEAN) TaskFlowPalette.SUNSET
                else TaskFlowPalette.OCEAN
            )
        }) {
            Box(
                modifier = Modifier.size(48.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(if (palette == TaskFlowPalette.OCEAN) "☀" else "◉",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}
