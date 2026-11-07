
package com.example.taskflow.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    // ==========================================
    // 1. REGISTRAR UN NUEVO USUARIO
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long


    // ==========================================
    // 2. BUSCAR USUARIO POR NOMBRE
    // ==========================================

    @Query(
        """
        SELECT * FROM users
        WHERE username = :username COLLATE NOCASE
        LIMIT 1
        """
    )
    suspend fun findByUsername(
        username: String
    ): UserEntity?


    // ==========================================
    // 3. BUSCAR USUARIO POR IDENTIFICADOR
    // ==========================================

    @Query(
        """
        SELECT * FROM users
        WHERE id = :userId
        LIMIT 1
        """
    )
    suspend fun findById(
        userId: Int
    ): UserEntity?


    // ==========================================
    // 4. COMPROBAR SI EXISTE UN USUARIO
    // ==========================================

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM users
            WHERE username = :username COLLATE NOCASE
        )
        """
    )
    suspend fun usernameExists(
        username: String
    ): Boolean
// ==========================================
// CONTAR USUARIOS REGISTRADOS
// ==========================================

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countUsers(): Int
}
