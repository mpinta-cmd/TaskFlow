
package com.example.taskflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.example.taskflow.model.TaskPriority


@Composable
fun NewTaskScreen(

    title: String,

    onTitleChange: (String) -> Unit,

    category: String,

    onCategoryChange: (String) -> Unit,

    selectedPriority: TaskPriority,

    onPriorityChange: (TaskPriority) -> Unit,

    isEditing: Boolean,

    onSave: () -> Unit,

    onBack: () -> Unit

) {

    Column(

        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),

        verticalArrangement = Arrangement.spacedBy(22.dp)

    ) {

        // VOLVER

        TextButton(
            onClick = onBack
        ) {

            Text("← Volver a mis tareas")

        }


        // ENCABEZADO

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(

                text = if (isEditing) {
                    "Editar tarea"
                } else {
                    "Nueva tarea"
                },

                style = MaterialTheme.typography.headlineMedium,

                fontWeight = FontWeight.Bold

            )


            Text(

                text = if (isEditing) {
                    "Modifica la información de tu actividad."
                } else {
                    "Organiza tus actividades y mantén el enfoque en tus objetivos."
                },

                style = MaterialTheme.typography.bodyMedium,

                color = MaterialTheme.colorScheme.onSurfaceVariant

            )

        }


        // FORMULARIO

        Card(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(24.dp),

            colors = CardDefaults.cardColors(

                containerColor = MaterialTheme.colorScheme.surface

            )

        ) {

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

                verticalArrangement = Arrangement.spacedBy(18.dp)

            ) {

                Text(

                    text = "Información de la tarea",

                    style = MaterialTheme.typography.titleLarge,

                    fontWeight = FontWeight.SemiBold

                )


                // ==================================
                // TÍTULO
                // ==================================

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Nombre de la tarea",
                        style = MaterialTheme.typography.titleSmall
                    )


                    OutlinedTextField(

                        value = title,

                        onValueChange = onTitleChange,

                        placeholder = {
                            Text("¿Qué necesitas hacer?")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(14.dp),

                        singleLine = true

                    )

                }


                // ==================================
                // CATEGORÍA
                // ==================================

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Categoría",
                        style = MaterialTheme.typography.titleSmall
                    )


                    OutlinedTextField(

                        value = category,

                        onValueChange = onCategoryChange,

                        placeholder = {
                            Text("Ej. Personal, Académica, Trabajo")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(14.dp),

                        singleLine = true

                    )

                }


                // ==================================
                // PRIORIDAD
                // ==================================

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Text(
                        "Nivel de prioridad",
                        style = MaterialTheme.typography.titleSmall
                    )


                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        TaskPriority.entries.forEach { priority ->

                            FilterChip(

                                selected =
                                    selectedPriority == priority,

                                onClick = {
                                    onPriorityChange(priority)
                                },

                                label = {

                                    Text(

                                        when (priority) {

                                            TaskPriority.HIGH -> "Alta"

                                            TaskPriority.MEDIUM -> "Media"

                                            TaskPriority.LOW -> "Baja"

                                        }

                                    )

                                }

                            )

                        }

                    }

                }

            }

        }


        // ==================================
        // GUARDAR
        // ==================================

        Button(

            onClick = onSave,

            enabled = title.isNotBlank() &&
                    category.isNotBlank(),

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),

            shape = RoundedCornerShape(16.dp)

        ) {

            Text(

                text = if (isEditing) {
                    "Guardar cambios"
                } else {
                    "Guardar tarea"
                },

                fontWeight = FontWeight.Bold

            )

        }


        // CANCELAR

        OutlinedButton(

            onClick = onBack,

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(16.dp)

        ) {

            Text("Cancelar")

        }

    }

}
