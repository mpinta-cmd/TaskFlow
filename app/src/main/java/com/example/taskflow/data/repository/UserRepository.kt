
package com.example.taskflow.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import android.util.Base64

import androidx.room.withTransaction

import com.example.taskflow.data.local.TaskDatabase
import com.example.taskflow.data.local.UserEntity

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


// ==============================================
// RESULTADO DE LAS OPERACIONES DE AUTENTICACIÓN
// ==============================================

sealed class UserAuthResult {

    data class Success(
        val user: UserEntity
    ) : UserAuthResult()

    data class Error(
        val message: String
    ) : UserAuthResult()

}


// ==============================================
// REPOSITORY DE USUARIOS
// ==============================================

class UserRepository(
    private val database: TaskDatabase
) {

    private val userDao = database.userDao()

    private val taskDao = database.taskDao()


    // ==========================================
    // 1. REGISTRAR USUARIO
    // ==========================================

    suspend fun registerUser(
        username: String,
        password: String
    ): UserAuthResult {

        // NORMALIZAR NOMBRE

        val normalizedUsername = username
            .trim()
            .lowercase(Locale.ROOT)


        // VALIDAR NOMBRE

        if (!normalizedUsername.matches(
                Regex("^[a-z0-9_]{3,20}$")
            )
        ) {

            return UserAuthResult.Error(
                "El usuario debe tener entre 3 y 20 caracteres. Usa letras, números o guion bajo."
            )

        }


        // VALIDAR CONTRASEÑA

        if (password.length < 4) {

            return UserAuthResult.Error(
                "La contraseña debe tener al menos 4 caracteres."
            )

        }


        // COMPROBAR SI EXISTE

        if (userDao.usernameExists(normalizedUsername)) {

            return UserAuthResult.Error(
                "Este nombre de usuario ya está registrado."
            )

        }


        // GENERAR SAL Y HASH

        val (passwordHash, salt) =
            PasswordHasher.create(password)


        // CREAR USUARIO

        val newUser = UserEntity(

            username = normalizedUsername,

            passwordHash = passwordHash,

            salt = salt

        )


        // ======================================
        // GUARDAR USUARIO Y ASIGNAR TAREAS
        // EN UNA SOLA TRANSACCIÓN
        // ======================================

        return try {

            val registeredUser = database.withTransaction {

                // INSERTAR USUARIO

                val generatedId = userDao.insertUser(newUser)

                val userId = generatedId.toInt()


                // COMPROBAR CUÁNTOS USUARIOS EXISTEN

                val totalUsers = userDao.countUsers()


                // SI ES EL PRIMER USUARIO,
                // RECUPERAR LAS TAREAS ANTIGUAS

                if (totalUsers == 1) {

                    taskDao.assignUnownedTasksToUser(userId)

                }


                // DEVOLVER USUARIO REGISTRADO

                newUser.copy(
                    id = userId
                )

            }


            UserAuthResult.Success(registeredUser)

        } catch (e: SQLiteConstraintException) {

            UserAuthResult.Error(
                "Este nombre de usuario ya está registrado."
            )

        }

    }


    // ==========================================
    // 2. INICIAR SESIÓN
    // ==========================================

    suspend fun loginUser(
        username: String,
        password: String
    ): UserAuthResult {

        val normalizedUsername = username
            .trim()
            .lowercase(Locale.ROOT)


        if (normalizedUsername.isBlank() || password.isEmpty()) {

            return UserAuthResult.Error(
                "Ingresa tu usuario y contraseña."
            )

        }


        // BUSCAR USUARIO

        val user = userDao.findByUsername(
            normalizedUsername
        ) ?: return UserAuthResult.Error(
            "Usuario o contraseña incorrectos."
        )


        // VERIFICAR CONTRASEÑA

        val passwordCorrect = PasswordHasher.verify(

            password = password,

            storedHash = user.passwordHash,

            storedSalt = user.salt

        )


        if (!passwordCorrect) {

            return UserAuthResult.Error(
                "Usuario o contraseña incorrectos."
            )

        }


        return UserAuthResult.Success(user)

    }

}


// ==============================================
// 3. PROCESAMIENTO SEGURO DE CONTRASEÑAS
// ==============================================

private object PasswordHasher {

    private const val SHA256_ITERATIONS = 210_000

    private const val SHA1_ITERATIONS = 600_000

    private const val KEY_LENGTH = 256


    // ==========================================
    // GENERAR HASH Y SAL
    // ==========================================

    fun create(password: String): Pair<String, String> {

        val saltBytes = ByteArray(16)

        SecureRandom().nextBytes(saltBytes)


        val useSha256 =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O


        val algorithm = if (useSha256) {

            "PBKDF2WithHmacSHA256"

        } else {

            "PBKDF2WithHmacSHA1"

        }


        val algorithmName = if (useSha256) {

            "pbkdf2-sha256"

        } else {

            "pbkdf2-sha1"

        }


        val iterations = if (useSha256) {

            SHA256_ITERATIONS

        } else {

            SHA1_ITERATIONS

        }


        val hashBytes = deriveKey(

            password,
            saltBytes,
            algorithm,
            iterations

        )


        val encodedSalt = Base64.encodeToString(
            saltBytes,
            Base64.NO_WRAP
        )


        val encodedHash = Base64.encodeToString(
            hashBytes,
            Base64.NO_WRAP
        )


        val storedHash =
            "$algorithmName:$iterations:$encodedHash"


        return Pair(storedHash, encodedSalt)

    }


    // ==========================================
    // VERIFICAR CONTRASEÑA
    // ==========================================

    fun verify(
        password: String,
        storedHash: String,
        storedSalt: String
    ): Boolean {

        return try {

            val parts = storedHash.split(":")

            if (parts.size != 3) {
                return false
            }


            val algorithm = when (parts[0]) {

                "pbkdf2-sha256" ->
                    "PBKDF2WithHmacSHA256"

                "pbkdf2-sha1" ->
                    "PBKDF2WithHmacSHA1"

                else -> return false

            }


            val iterations = parts[1].toIntOrNull()
                ?: return false


            if (iterations !in 1..1_000_000) {
                return false
            }


            val expectedHash = Base64.decode(
                parts[2],
                Base64.NO_WRAP
            )


            val saltBytes = Base64.decode(
                storedSalt,
                Base64.NO_WRAP
            )


            val actualHash = deriveKey(

                password,
                saltBytes,
                algorithm,
                iterations

            )


            MessageDigest.isEqual(
                expectedHash,
                actualHash
            )

        } catch (e: Exception) {

            false

        }

    }


    // ==========================================
    // DERIVAR CLAVE
    // ==========================================

    private fun deriveKey(
        password: String,
        salt: ByteArray,
        algorithm: String,
        iterations: Int
    ): ByteArray {

        val specification = PBEKeySpec(

            password.toCharArray(),

            salt,

            iterations,

            KEY_LENGTH

        )


        return try {

            SecretKeyFactory
                .getInstance(algorithm)
                .generateSecret(specification)
                .encoded

        } finally {

            specification.clearPassword()

        }

    }

}
