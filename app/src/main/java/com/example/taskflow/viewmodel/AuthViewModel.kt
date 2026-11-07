
package com.example.taskflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.taskflow.data.local.UserEntity
import com.example.taskflow.data.repository.UserRepository
import com.example.taskflow.data.repository.UserAuthResult

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch


// ==============================================
// 1. ESTADO DE AUTENTICACIÓN
// ==============================================

data class AuthUiState(

    val currentUser: UserEntity? = null,

    val isLoading: Boolean = false,

    val error: String? = null,

    val message: String? = null

)


// ==============================================
// 2. AUTH VIEWMODEL
// ==============================================

class AuthViewModel(

    private val userRepository: UserRepository

) : ViewModel() {


    // ==========================================
    // ESTADO PRIVADO
    // ==========================================

    private val _authState = MutableStateFlow(
        AuthUiState()
    )


    // ==========================================
    // ESTADO PÚBLICO
    // ==========================================

    val authState: StateFlow<AuthUiState> =
        _authState.asStateFlow()


    // ==========================================
    // 3. REGISTRAR USUARIO
    // ==========================================

    fun register(
        username: String,
        password: String
    ) {

        if (_authState.value.isLoading) return

        viewModelScope.launch {

            _authState.value = AuthUiState(
                isLoading = true
            )


            when (
                val result = userRepository.registerUser(
                    username = username,
                    password = password
                )
            ) {

                is UserAuthResult.Success -> {

                    _authState.value = AuthUiState(

                        currentUser = result.user,

                        isLoading = false,

                        message = "Cuenta creada correctamente."

                    )

                }

                is UserAuthResult.Error -> {

                    _authState.value = AuthUiState(

                        isLoading = false,

                        error = result.message

                    )

                }

            }

        }

    }


    // ==========================================
    // 4. INICIAR SESIÓN
    // ==========================================

    fun login(
        username: String,
        password: String
    ) {

        if (_authState.value.isLoading) return

        viewModelScope.launch {

            _authState.value = AuthUiState(
                isLoading = true
            )


            when (
                val result = userRepository.loginUser(
                    username = username,
                    password = password
                )
            ) {

                is UserAuthResult.Success -> {

                    _authState.value = AuthUiState(

                        currentUser = result.user,

                        isLoading = false,

                        message = "Bienvenido, ${result.user.username}."

                    )

                }

                is UserAuthResult.Error -> {

                    _authState.value = AuthUiState(

                        isLoading = false,

                        error = result.message

                    )

                }

            }

        }

    }


    // ==========================================
    // 5. CERRAR SESIÓN
    // ==========================================

    fun logout() {

        _authState.value = AuthUiState()

    }


    // ==========================================
    // 6. LIMPIAR MENSAJES
    // ==========================================

    fun clearFeedback() {

        _authState.value = _authState.value.copy(

            error = null,

            message = null

        )

    }

}
