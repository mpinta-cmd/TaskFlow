
package com.example.taskflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.taskflow.model.Task
import com.example.taskflow.model.TaskPriority


@Composable
fun TaskDetailScreen(

    task: Task?,

    onBack: () -> Unit,

    onEdit: () -> Unit

) {

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement = Arrangement.spacedBy(20.dp)

    ) {

        // ENCABEZADO

        Text(
            text = "Detalle de tarea",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )


        if (task == null) {

            Text(
                text = "No se encontró la tarea.",
                color = MaterialTheme.colorScheme.error
            )

        } else {

            // INFORMACIÓN DE LA TAREA

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(

                    modifier = Modifier.padding(22.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(16.dp)

                ) {

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )


                    Text(
                        text = "Identificador: ${task.id}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )


                    Text(
                        text = "Categoría: ${task.category}",
                        style = MaterialTheme.typography.bodyLarge
                    )


                    // PRIORIDAD

                    val priorityText = when (task.priority) {

                        TaskPriority.HIGH -> "Alta"

                        TaskPriority.MEDIUM -> "Media"

                        TaskPriority.LOW -> "Baja"

                    }

                    Text(
                        text = "Prioridad: $priorityText",
                        style = MaterialTheme.typography.bodyLarge
                    )


                    // ESTADO

                    val statusText =
                        if (task.isCompleted) {
                            "Completada"
                        } else {
                            "Pendiente"
                        }

                    Text(
                        text = "Estado: $statusText",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (task.isCompleted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                }

            }


            // BOTÓN EDITAR

            Button(

                onClick = onEdit,

                modifier = Modifier.fillMaxWidth()

            ) {

                Text(
                    text = "✏️  Editar tarea",
                    fontWeight = FontWeight.SemiBold
                )

            }

        }


        // REGRESAR

        OutlinedButton(

            onClick = onBack,

            modifier = Modifier.fillMaxWidth()

        ) {

            Text("Volver a mis tareas")

        }

    }

}


// ==============================================
// PREVIEW
// ==============================================

@Preview(showBackground = true)
@Composable
fun TaskDetailScreenPreview() {

    TaskDetailScreen(

        task = Task(
            1,
            "Estudiar programación",
            "Académica",
            TaskPriority.MEDIUM
        ),

        onBack = {},

        onEdit = {}

    )

}
