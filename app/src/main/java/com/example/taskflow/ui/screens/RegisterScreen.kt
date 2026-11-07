
package com.example.taskflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

import com.example.taskflow.viewmodel.AuthUiState


@Composable
fun RegisterScreen(

    authState: AuthUiState,

    onRegister: (String, String) -> Unit,

    onBackToLogin: () -> Unit,

    onClearFeedback: () -> Unit

) {

    var username by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var showPassword by remember {
        mutableStateOf(false)
    }

    var validationError by remember {
        mutableStateOf<String?>(null)
    }


    Column(

        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center

    ) {

        Spacer(modifier = Modifier.height(20.dp))


        // ======================================
        // ENCABEZADO
        // ======================================

        Text(
            text = "TASKFLOW",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Crea tu espacio personal de organización.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )


        Spacer(modifier = Modifier.height(28.dp))


        // ======================================
        // FORMULARIO
        // ======================================

        Card(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(24.dp),

            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),

            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )

        ) {

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),

                verticalArrangement = Arrangement.spacedBy(17.dp)

            ) {

                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Regístrate para comenzar a organizar tus actividades.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )


                // USUARIO

                OutlinedTextField(

                    value = username,

                    onValueChange = {
                        username = it
                        validationError = null
                        onClearFeedback()
                    },

                    label = {
                        Text("Nombre de usuario")
                    },

                    placeholder = {
                        Text("Ejemplo: carlos123")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    shape = RoundedCornerShape(14.dp)

                )


                // CONTRASEÑA

                OutlinedTextField(

                    value = password,

                    onValueChange = {
                        password = it
                        validationError = null
                        onClearFeedback()
                    },

                    label = {
                        Text("Contraseña")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    shape = RoundedCornerShape(14.dp),

                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),

                    trailingIcon = {

                        TextButton(
                            onClick = {
                                showPassword = !showPassword
                            }
                        ) {

                            Text(
                                if (showPassword) "Ocultar"
                                else "Mostrar"
                            )

                        }

                    }

                )


                // CONFIRMAR CONTRASEÑA

                OutlinedTextField(

                    value = confirmPassword,

                    onValueChange = {
                        confirmPassword = it
                        validationError = null
                        onClearFeedback()
                    },

                    label = {
                        Text("Confirmar contraseña")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    shape = RoundedCornerShape(14.dp),

                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )

                )


                // VALIDACIÓN LOCAL

                validationError?.let { error ->

                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )

                }


                // ERROR DEL REPOSITORY

                authState.error?.let { error ->

                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )

                }


                // BOTÓN REGISTRAR

                Button(

                    onClick = {

                        validationError = when {

                            username.isBlank() ->
                                "Ingresa un nombre de usuario."

                            password.length < 4 ->
                                "La contraseña debe tener al menos 4 caracteres."

                            password != confirmPassword ->
                                "Las contraseñas no coinciden."

                            else -> null

                        }


                        if (validationError == null) {

                            onRegister(
                                username,
                                password
                            )

                        }

                    },

                    enabled = !authState.isLoading,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),

                    shape = RoundedCornerShape(15.dp)

                ) {

                    if (authState.isLoading) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(23.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )

                    } else {

                        Text(
                            "Crear cuenta",
                            fontWeight = FontWeight.Bold
                        )

                    }

                }


                // VOLVER AL LOGIN

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement = Arrangement.Center,

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Text(
                        "¿Ya tienes una cuenta?",
                        style = MaterialTheme.typography.bodySmall
                    )

                    TextButton(
                        onClick = onBackToLogin
                    ) {

                        Text("Iniciar sesión")

                    }

                }

            }

        }

    }

}
