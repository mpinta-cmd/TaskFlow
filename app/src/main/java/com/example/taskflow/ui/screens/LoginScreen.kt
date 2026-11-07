
package com.example.taskflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

import com.example.taskflow.viewmodel.AuthUiState


@Composable
fun LoginScreen(

    authState: AuthUiState,

    onLogin: (String, String) -> Unit,

    onNavigateToRegister: () -> Unit,

    onClearFeedback: () -> Unit

) {

    var username by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var showPassword by remember {
        mutableStateOf(false)
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

        Spacer(modifier = Modifier.height(30.dp))


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
            text = "Organiza tu tiempo. Alcanza tus metas.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )


        Spacer(modifier = Modifier.height(36.dp))


        // ======================================
        // TARJETA DE INICIO DE SESIÓN
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

                verticalArrangement = Arrangement.spacedBy(18.dp)

            ) {

                Text(
                    text = "¡Bienvenido!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Ingresa a tu cuenta para consultar tus tareas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )


                // USUARIO

                OutlinedTextField(

                    value = username,

                    onValueChange = {
                        username = it
                        onClearFeedback()
                    },

                    label = {
                        Text("Nombre de usuario")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    shape = RoundedCornerShape(14.dp),

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )

                )


                // CONTRASEÑA

                OutlinedTextField(

                    value = password,

                    onValueChange = {
                        password = it
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


                // MENSAJE DE ERROR

                authState.error?.let { error ->

                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )

                }


                // BOTÓN INGRESAR

                Button(

                    onClick = {
                        onLogin(username, password)
                    },

                    enabled = username.isNotBlank() &&
                            password.isNotEmpty() &&
                            !authState.isLoading,

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
                            "Iniciar sesión",
                            fontWeight = FontWeight.Bold
                        )

                    }

                }


                // IR AL REGISTRO

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement = Arrangement.Center,

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Text(
                        "¿No tienes una cuenta?",
                        style = MaterialTheme.typography.bodySmall
                    )

                    TextButton(
                        onClick = onNavigateToRegister
                    ) {

                        Text(
                            "Regístrate"
                        )

                    }

                }

            }

        }


        Spacer(modifier = Modifier.height(28.dp))


        Text(
            text = "TASKFLOW · ORGANIZA TU DÍA",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

    }

}
