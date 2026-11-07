
package com.example.taskflow.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(
            value = ["username"],
            unique = true
        )
    ]
)
data class UserEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val username: String,

    // Contraseña almacenada como hash, nunca en texto plano
    val passwordHash: String,

    // Salt individual para proteger cada contraseña
    val salt: String

)
