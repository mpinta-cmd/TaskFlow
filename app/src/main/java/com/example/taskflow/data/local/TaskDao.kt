
package com.example.taskflow.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import kotlinx.coroutines.flow.Flow


/*
 DAO: Data Access Object

 CRUD:
 C - CREATE
 R - READ
 U - UPDATE
 D - DELETE

 Todas las operaciones se realizan
 respetando el propietario de cada tarea.
*/


@Dao
interface TaskDao {


    // ==========================================
    // 1. OBTENER TAREAS DE UN USUARIO
    // ==========================================

    @Query(
        """
        SELECT * FROM tasks
        WHERE userId = :userId
        ORDER BY id DESC
        """
    )
    fun observeByUser(
        userId: Int
    ): Flow<List<TaskEntity>>


    // ==========================================
    // 2. BUSCAR TAREA POR ID Y PROPIETARIO
    // ==========================================

    @Query(
        """
        SELECT * FROM tasks
        WHERE id = :taskId
        AND userId = :userId
        LIMIT 1
        """
    )
    suspend fun findByIdForUser(
        taskId: Int,
        userId: Int
    ): TaskEntity?


    // ==========================================
    // 3. INSERTAR NUEVA TAREA
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(
        task: TaskEntity
    ): Long


    // ==========================================
    // 4. ACTUALIZAR TAREA DE UN USUARIO
    // ==========================================

    @Query(
        """
        UPDATE tasks
        SET
            title = :title,
            category = :category,
            priority = :priority,
            isCompleted = :isCompleted
        WHERE id = :taskId
        AND userId = :userId
        """
    )
    suspend fun updateForUser(
        taskId: Int,
        userId: Int,
        title: String,
        category: String,
        priority: String,
        isCompleted: Boolean
    ): Int


    // ==========================================
    // 5. ELIMINAR TAREA DE UN USUARIO
    // ==========================================

    @Query(
        """
        DELETE FROM tasks
        WHERE id = :taskId
        AND userId = :userId
        """
    )
    suspend fun deleteForUser(
        taskId: Int,
        userId: Int
    ): Int


    // ==========================================
    // 6. ELIMINAR TODAS LAS TAREAS DEL USUARIO
    // ==========================================

    @Query(
        """
        DELETE FROM tasks
        WHERE userId = :userId
        """
    )
    suspend fun deleteAllForUser(
        userId: Int
    ): Int
// ==========================================
// ASIGNAR TAREAS ANTIGUAS AL PRIMER USUARIO
// ==========================================

    @Query("""
    UPDATE tasks
    SET userId = :userId
    WHERE userId IS NULL
""")
    suspend fun assignUnownedTasksToUser(
        userId: Int
    ): Int
}
