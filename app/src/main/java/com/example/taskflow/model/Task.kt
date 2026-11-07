package com.example.taskflow.model

data class Task(
    val id: Int,
    val title: String,
    val category: String,
    val priority: TaskPriority,
    val isCompleted: Boolean = false
)

enum class TaskPriority { HIGH, MEDIUM, LOW }
enum class TaskFilter { ALL, PENDING, COMPLETED }
