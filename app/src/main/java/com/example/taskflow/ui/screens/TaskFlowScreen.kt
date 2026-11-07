
package com.example.taskflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.example.taskflow.model.Task
import com.example.taskflow.model.TaskFilter

import com.example.taskflow.ui.theme.Muted
import com.example.taskflow.ui.theme.TaskFlowPalette

import com.example.taskflow.ui.components.TaskHeader
import com.example.taskflow.ui.components.ProgressCard
import com.example.taskflow.ui.components.StatisticsSection
import com.example.taskflow.ui.components.TaskFilterBar
import com.example.taskflow.ui.components.TaskCard
import com.example.taskflow.ui.components.EmptyTasksState


@Composable
fun TaskFlowScreen(

    tasks: List<Task>,

    selectedFilter: TaskFilter,

    onFilterChange: (TaskFilter) -> Unit,

    onToggleTask: (Int) -> Unit,

    onDeleteTask: (Int) -> Unit,

    // NUEVO: SELECCIONAR UNA TAREA
    onTaskClick: (Int) -> Unit,

    onNewTaskClick: () -> Unit,

    palette: TaskFlowPalette,

    onPaletteChange: (TaskFlowPalette) -> Unit

) {

    // ==========================================
    // 1. ESTADÍSTICAS
    // ==========================================

    val completed = tasks.count { it.isCompleted }

    val pending = tasks.size - completed

    val progress =
        if (tasks.isEmpty()) 0f
        else completed.toFloat() / tasks.size


    // ==========================================
    // 2. FILTRAR TAREAS
    // ==========================================

    val visible = when (selectedFilter) {

        TaskFilter.ALL -> tasks

        TaskFilter.PENDING ->
            tasks.filter { !it.isCompleted }

        TaskFilter.COMPLETED ->
            tasks.filter { it.isCompleted }

    }


    // ==========================================
    // 3. CONTENEDOR PRINCIPAL
    // ==========================================

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(

            modifier = Modifier.fillMaxSize(),

            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 28.dp,
                bottom = 108.dp
            ),

            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {

            // ENCABEZADO

            item {
                TaskHeader(
                    palette,
                    onPaletteChange
                )
            }


            // PROGRESO

            item {
                ProgressCard(
                    progress,
                    completed,
                    tasks.size
                )
            }


            // ESTADÍSTICAS

            item {
                StatisticsSection(
                    tasks.size,
                    pending,
                    completed
                )
            }


            // TÍTULO DE LA SECCIÓN

            item {

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    Text(
                        "Mis tareas",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        "Mantén el enfoque en lo importante.",
                        color = Muted,
                        style = MaterialTheme.typography.bodyMedium
                    )

                }
            }


            // FILTROS

            item {
                TaskFilterBar(
                    selectedFilter,
                    onFilterChange
                )
            }


            // ==================================
            // LISTA DE TAREAS
            // ==================================

            if (visible.isEmpty()) {

                item {
                    EmptyTasksState()
                }

            } else {

                items(
                    visible,
                    key = { it.id }
                ) { task ->

                    TaskCard(

                        task = task,

                        onToggle = {
                            onToggleTask(task.id)
                        },

                        onDelete = {
                            onDeleteTask(task.id)
                        },

                        // NUEVO: ENVIAR EL ID
                        onClick = {
                            onTaskClick(task.id)
                        }

                    )
                }
            }
        }


        // ======================================
        // 4. BOTÓN NUEVA TAREA
        // ======================================

        FloatingActionButton(

            onClick = onNewTaskClick,

            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(22.dp),

            containerColor = MaterialTheme.colorScheme.primary,

            contentColor = Color.White,

            shape = RoundedCornerShape(20.dp)

        ) {

            Text(
                "＋  Nueva tarea",
                modifier = Modifier.padding(horizontal = 18.dp),
                fontWeight = FontWeight.SemiBold
            )

        }

    }
}
