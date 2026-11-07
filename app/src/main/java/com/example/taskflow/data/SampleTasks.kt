package com.example.taskflow.data

import com.example.taskflow.model.Task
import com.example.taskflow.model.TaskPriority

// Datos de ejemplo de la clase. En la versión final del video todas comienzan pendientes.
val sampleTasks = listOf(
    Task(1, "Preparar presentación del proyecto", "Universidad", TaskPriority.HIGH),
    Task(2, "Revisar documentación de Compose", "Aprendizaje", TaskPriority.MEDIUM),
    Task(3, "Organizar repositorio GitHub", "Desarrollo", TaskPriority.LOW),
    Task(4, "Planificar actividades de la semana", "Personal", TaskPriority.HIGH),
    Task(5, "Enviar avances del proyecto", "Universidad", TaskPriority.MEDIUM),
    Task(6, "Leer sobre arquitectura Android", "Aprendizaje", TaskPriority.LOW)
)
