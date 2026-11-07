
package com.example.taskflow.data.repository

import com.example.taskflow.data.local.TaskDao
import com.example.taskflow.data.local.TaskEntity
import com.example.taskflow.model.Task
import com.example.taskflow.model.TaskPriority

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TaskRepository(
    private val dao: TaskDao
) {


    // ==========================================
    // 1. READ: OBTENER TAREAS DE UN USUARIO
    // ==========================================

    fun observeTasks(userId: Int): Flow<List<Task>> {

        return dao.observeByUser(userId).map { entities ->

            entities.map { entity ->

                entity.toModel()

            }

        }

    }


    // ==========================================
    // 2. CREATE: AGREGAR UNA TAREA
    // ==========================================

    suspend fun addTask(
        userId: Int,
        title: String,
        priority: TaskPriority,
        category: String = "Personal"
    ) {

        val cleanTitle = title.trim()

        if (cleanTitle.isBlank()) return


        val entity = TaskEntity(

            title = cleanTitle,

            category = category,

            priority = priority.name,

            isCompleted = false,

            // ASIGNAR PROPIETARIO

            userId = userId

        )


        dao.insert(entity)

    }


    // ==========================================
    // 3. UPDATE: COMPLETAR O REABRIR UNA TAREA
    // ==========================================

    suspend fun toggleTask(
        userId: Int,
        taskId: Int
    ) {

        // BUSCAR SOLAMENTE SI PERTENECE AL USUARIO

        val entity = dao.findByIdForUser(
            taskId = taskId,
            userId = userId
        ) ?: return


        // ACTUALIZAR ESTADO

        dao.updateForUser(

            taskId = taskId,

            userId = userId,

            title = entity.title,

            category = entity.category,

            priority = entity.priority,

            isCompleted = !entity.isCompleted

        )

    }


    // ==========================================
    // 4. UPDATE: EDITAR UNA TAREA
    // ==========================================

    suspend fun updateTask(
        userId: Int,
        taskId: Int,
        title: String,
        category: String,
        priority: TaskPriority
    ) {

        val cleanTitle = title.trim()

        if (cleanTitle.isBlank()) return


        // COMPROBAR PROPIETARIO

        val entity = dao.findByIdForUser(
            taskId = taskId,
            userId = userId
        ) ?: return


        // GUARDAR CAMBIOS

        dao.updateForUser(

            taskId = taskId,

            userId = userId,

            title = cleanTitle,

            category = category,

            priority = priority.name,

            isCompleted = entity.isCompleted

        )

    }


    // ==========================================
    // 5. DELETE: ELIMINAR UNA TAREA
    // ==========================================

    suspend fun deleteTask(
        userId: Int,
        taskId: Int
    ) {

        dao.deleteForUser(

            taskId = taskId,

            userId = userId

        )

    }


    // ==========================================
    // 6. DELETE: VACIAR TAREAS DE UN USUARIO
    // ==========================================

    suspend fun deleteAllTasks(
        userId: Int
    ) {

        dao.deleteAllForUser(userId)

    }

}


// ==============================================
// 7. CONVERTIR ENTITY A MODEL
// ==============================================

private fun TaskEntity.toModel(): Task {

    return Task(

        id = id,

        title = title,

        category = category,

        priority = TaskPriority.entries.firstOrNull {

            it.name == priority

        } ?: TaskPriority.MEDIUM,

        isCompleted = isCompleted

    )

}
