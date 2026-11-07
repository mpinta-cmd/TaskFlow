
package com.example.taskflow

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// ==============================================
// BASE DE DATOS
// ==============================================

import com.example.taskflow.data.local.TaskDatabase

// ==============================================
// REPOSITORIES
// ==============================================

import com.example.taskflow.data.repository.TaskRepository
import com.example.taskflow.data.repository.UserRepository

// ==============================================
// MODELOS
// ==============================================

import com.example.taskflow.data.sampleTasks
import com.example.taskflow.model.TaskFilter
import com.example.taskflow.model.TaskPriority

// ==============================================
// PANTALLAS
// ==============================================

import com.example.taskflow.ui.screens.TaskFlowScreen
import com.example.taskflow.ui.screens.TaskDetailScreen
import com.example.taskflow.ui.screens.NewTaskScreen
import com.example.taskflow.ui.screens.LoginScreen
import com.example.taskflow.ui.screens.RegisterScreen

// ==============================================
// TEMA
// ==============================================

import com.example.taskflow.ui.theme.TaskFlowPalette
import com.example.taskflow.ui.theme.TaskFlowTheme

// ==============================================
// VIEWMODELS
// ==============================================

import com.example.taskflow.viewmodel.TaskViewModel
import com.example.taskflow.viewmodel.TaskViewModelFactory

import com.example.taskflow.viewmodel.AuthViewModel
import com.example.taskflow.viewmodel.AuthViewModelFactory


// ==============================================
// 1. RUTAS DE NAVEGACIÓN
// ==============================================

private object TaskRoutes {

    const val LOGIN = "login"

    const val REGISTER = "register"

    const val LIST = "tasks"

    const val DETAIL = "task_detail/{taskId}"

    const val NEW_TASK = "new_task"

    fun detail(taskId: Int): String {
        return "task_detail/$taskId"
    }

}


// ==============================================
// 2. MAIN ACTIVITY
// ==============================================

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // ======================================
        // OBTENER BASE DE DATOS
        // ======================================

        val database = TaskDatabase.getDatabase(
            applicationContext
        )


        // ======================================
        // CREAR TASK REPOSITORY
        // ======================================

        val taskRepository = TaskRepository(
            database.taskDao()
        )


        // ======================================
        // CREAR USER REPOSITORY
        // ======================================

        val userRepository = UserRepository(
            database
        )


        // ======================================
        // CREAR FACTORIES
        // ======================================

        val taskFactory = TaskViewModelFactory(
            taskRepository
        )

        val authFactory = AuthViewModelFactory(
            userRepository
        )


        // ======================================
        // INICIAR APLICACIÓN
        // ======================================

        setContent {

            val taskViewModel: TaskViewModel = viewModel(
                factory = taskFactory
            )

            val authViewModel: AuthViewModel = viewModel(
                factory = authFactory
            )

            TaskFlowDemo(

                taskViewModel = taskViewModel,

                authViewModel = authViewModel

            )

        }

    }

}


// ==============================================
// 3. TASKFLOW DEMO
// ==============================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFlowDemo(

    taskViewModel: TaskViewModel,

    authViewModel: AuthViewModel

) {

    // ==========================================
    // 1. OBSERVAR AUTENTICACIÓN
    // ==========================================

    val authState by authViewModel.authState
        .collectAsStateWithLifecycle()


    // ==========================================
    // 2. OBSERVAR TAREAS
    // ==========================================

    val tasks by taskViewModel.tasks
        .collectAsStateWithLifecycle()


    // ==========================================
    // 3. OBSERVAR FILTROS
    // ==========================================

    val selectedFilter by taskViewModel.selectedFilter
        .collectAsStateWithLifecycle()


    // ==========================================
    // 4. ESTADO DE PALETA
    // ==========================================

    var palette by remember {

        mutableStateOf(TaskFlowPalette.OCEAN)

    }


    // ==========================================
    // 5. ESTADOS DEL FORMULARIO
    // ==========================================

    var newTaskTitle by remember {

        mutableStateOf("")

    }

    var newTaskCategory by remember {

        mutableStateOf("Personal")

    }

    var newTaskPriority by remember {

        mutableStateOf(TaskPriority.MEDIUM)

    }


    // ==========================================
    // 6. CONTROL DE EDICIÓN
    // ==========================================

    // Si es null, se está creando una tarea.
    // Si tiene un ID, se está editando una existente.

    var editingTaskId by remember {

        mutableStateOf<Int?>(null)

    }


    // ==========================================
    // 7. CONTROLADOR DE NAVEGACIÓN
    // ==========================================

    val navController = rememberNavController()


    // ==========================================
    // 8. OBSERVAR RUTA ACTUAL
    // ==========================================

    val backStackEntry by navController
        .currentBackStackEntryAsState()

    val currentRoute = backStackEntry
        ?.destination
        ?.route


    // ==========================================
    // 9. DETECTAR USUARIO AUTENTICADO
    // ==========================================

    LaunchedEffect(authState.currentUser?.id) {

        val user = authState.currentUser

        if (user != null) {

            // ESTABLECER USUARIO ACTIVO

            taskViewModel.setUserId(user.id)


            // IR A LA PANTALLA PRINCIPAL

            navController.navigate(TaskRoutes.LIST) {

                popUpTo(TaskRoutes.LOGIN) {

                    inclusive = true

                }

                launchSingleTop = true

            }

        }

    }


    // ==========================================
    // 10. TEMA PRINCIPAL
    // ==========================================

    TaskFlowTheme(

        palette = palette

    ) {

        Scaffold(

            modifier = Modifier.fillMaxSize(),

            // ==================================
            // BARRA SUPERIOR
            // ==================================

            topBar = {

                if (currentRoute == TaskRoutes.LIST) {

                    TopAppBar(

                        title = {

                            Text(

                                text = "Hola, ${authState.currentUser?.username ?: "usuario"}"

                            )

                        },

                        actions = {

                            TextButton(

                                onClick = {

                                    // LIMPIAR USUARIO ACTIVO

                                    taskViewModel.clearUser()

                                    authViewModel.logout()


                                    // LIMPIAR FORMULARIO

                                    editingTaskId = null

                                    newTaskTitle = ""

                                    newTaskCategory = "Personal"

                                    newTaskPriority = TaskPriority.MEDIUM


                                    // REGRESAR AL LOGIN

                                    navController.navigate(
                                        TaskRoutes.LOGIN
                                    ) {

                                        popUpTo(TaskRoutes.LIST) {

                                            inclusive = true

                                        }

                                        launchSingleTop = true

                                    }

                                }

                            ) {

                                Text("Cerrar sesión")

                            }

                        }

                    )

                }

            }

        ) { innerPadding ->


            Surface(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),

                color = MaterialTheme.colorScheme.background

            ) {


                // ==================================
                // 11. NAVEGACIÓN PRINCIPAL
                // ==================================

                NavHost(

                    navController = navController,

                    startDestination = TaskRoutes.LOGIN

                ) {


                    // ==================================
                    // LOGIN
                    // ==================================

                    composable(TaskRoutes.LOGIN) {

                        LoginScreen(

                            authState = authState,

                            onLogin = { username, password ->

                                authViewModel.login(
                                    username,
                                    password
                                )

                            },

                            onNavigateToRegister = {

                                authViewModel.clearFeedback()

                                navController.navigate(
                                    TaskRoutes.REGISTER
                                )

                            },

                            onClearFeedback = {

                                authViewModel.clearFeedback()

                            }

                        )

                    }


                    // ==================================
                    // REGISTRO
                    // ==================================

                    composable(TaskRoutes.REGISTER) {

                        RegisterScreen(

                            authState = authState,

                            onRegister = { username, password ->

                                authViewModel.register(
                                    username,
                                    password
                                )

                            },

                            onBackToLogin = {

                                authViewModel.clearFeedback()

                                navController.popBackStack()

                            },

                            onClearFeedback = {

                                authViewModel.clearFeedback()

                            }

                        )

                    }


                    // ==================================
                    // PANTALLA PRINCIPAL
                    // ==================================

                    composable(TaskRoutes.LIST) {

                        TaskFlowScreen(

                            tasks = tasks,

                            selectedFilter = selectedFilter,

                            onFilterChange = taskViewModel::changeFilter,

                            onToggleTask = taskViewModel::toggleTask,

                            onDeleteTask = taskViewModel::deleteTask,


                            // NAVEGAR AL DETALLE

                            onTaskClick = { taskId ->

                                navController.navigate(
                                    TaskRoutes.detail(taskId)
                                )

                            },


                            // CREAR NUEVA TAREA

                            onNewTaskClick = {

                                // ASEGURAR MODO CREACIÓN

                                editingTaskId = null


                                // LIMPIAR FORMULARIO

                                newTaskTitle = ""

                                newTaskCategory = "Personal"

                                newTaskPriority = TaskPriority.MEDIUM


                                // ABRIR FORMULARIO

                                navController.navigate(
                                    TaskRoutes.NEW_TASK
                                )

                            },


                            palette = palette,

                            onPaletteChange = { newPalette ->

                                palette = newPalette

                            }

                        )

                    }


                    // ==================================
                    // DETALLE DE TAREA
                    // ==================================

                    composable(

                        route = TaskRoutes.DETAIL,

                        arguments = listOf(

                            navArgument("taskId") {

                                type = NavType.IntType

                            }

                        )

                    ) { backStackEntry ->


                        val taskId = backStackEntry.arguments
                            ?.getInt("taskId")


                        // BUSCAR TAREA DEL USUARIO ACTUAL

                        val selectedTask = tasks.firstOrNull {

                            it.id == taskId

                        }


                        TaskDetailScreen(

                            task = selectedTask,


                            // REGRESAR

                            onBack = {

                                navController.popBackStack()

                            },


                            // ==================================
                            // EDITAR TAREA
                            // ==================================

                            onEdit = {

                                selectedTask?.let { task ->

                                    // CARGAR INFORMACIÓN EXISTENTE

                                    newTaskTitle = task.title

                                    newTaskCategory = task.category

                                    newTaskPriority = task.priority


                                    // GUARDAR IDENTIFICADOR

                                    editingTaskId = task.id


                                    // ABRIR FORMULARIO

                                    navController.navigate(
                                        TaskRoutes.NEW_TASK
                                    )

                                }

                            }

                        )

                    }


                    // ==================================
                    // CREAR O EDITAR TAREA
                    // ==================================

                    composable(TaskRoutes.NEW_TASK) {

                        NewTaskScreen(

                            // ==================================
                            // TÍTULO
                            // ==================================

                            title = newTaskTitle,

                            onTitleChange = { newTitle ->

                                newTaskTitle = newTitle

                            },


                            // ==================================
                            // CATEGORÍA
                            // ==================================

                            category = newTaskCategory,

                            onCategoryChange = { newCategory ->

                                newTaskCategory = newCategory

                            },


                            // ==================================
                            // PRIORIDAD
                            // ==================================

                            selectedPriority = newTaskPriority,

                            onPriorityChange = { newPriority ->

                                newTaskPriority = newPriority

                            },


                            // ==================================
                            // DETECTAR MODO
                            // ==================================

                            isEditing = editingTaskId != null,


                            // ==================================
                            // GUARDAR TAREA O CAMBIOS
                            // ==================================

                            onSave = {

                                if (
                                    newTaskTitle.isNotBlank() &&
                                    newTaskCategory.isNotBlank()
                                ) {

                                    val taskId = editingTaskId


                                    if (taskId != null) {

                                        // ==========================
                                        // ACTUALIZAR TAREA
                                        // ==========================

                                        taskViewModel.updateTask(

                                            taskId = taskId,

                                            title = newTaskTitle.trim(),

                                            category = newTaskCategory.trim(),

                                            priority = newTaskPriority

                                        )

                                    } else {

                                        // ==========================
                                        // CREAR TAREA
                                        // ==========================

                                        taskViewModel.addTask(

                                            title = newTaskTitle.trim(),

                                            priority = newTaskPriority,

                                            category = newTaskCategory.trim()

                                        )

                                    }


                                    // LIMPIAR ESTADOS

                                    editingTaskId = null

                                    newTaskTitle = ""

                                    newTaskCategory = "Personal"

                                    newTaskPriority = TaskPriority.MEDIUM


                                    // REGRESAR A LA PANTALLA ANTERIOR

                                    navController.popBackStack()

                                }

                            },


                            // ==================================
                            // CANCELAR O REGRESAR
                            // ==================================

                            onBack = {

                                editingTaskId = null

                                newTaskTitle = ""

                                newTaskCategory = "Personal"

                                newTaskPriority = TaskPriority.MEDIUM

                                navController.popBackStack()

                            }

                        )

                    }

                }

            }

        }

    }

}


// ==============================================
// 12. PREVIEW INDEPENDIENTE DE ROOM
// ==============================================

@Preview(

    showBackground = true,

    showSystemUi = true

)

@Composable
fun TaskFlowPreview() {

    var selectedFilter by remember {

        mutableStateOf(TaskFilter.ALL)

    }

    var palette by remember {

        mutableStateOf(TaskFlowPalette.OCEAN)

    }


    TaskFlowTheme(

        palette = palette

    ) {

        Surface(

            modifier = Modifier.fillMaxSize(),

            color = MaterialTheme.colorScheme.background

        ) {

            TaskFlowScreen(

                tasks = sampleTasks,

                selectedFilter = selectedFilter,

                onFilterChange = { filter ->

                    selectedFilter = filter

                },

                onToggleTask = {},

                onDeleteTask = {},

                onTaskClick = {},

                onNewTaskClick = {},

                palette = palette,

                onPaletteChange = { newPalette ->

                    palette = newPalette

                }

            )

        }

    }

}
