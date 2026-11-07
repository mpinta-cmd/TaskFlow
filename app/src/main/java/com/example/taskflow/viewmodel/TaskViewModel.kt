
package com.example.taskflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.example.taskflow.data.repository.TaskRepository
import com.example.taskflow.model.Task
import com.example.taskflow.model.TaskFilter
import com.example.taskflow.model.TaskPriority

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


// ==============================================
// VIEWMODEL
// ==============================================

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {


    // ==========================================
    // 1. USUARIO ACTUAL
    // ==========================================

    private val _currentUserId = MutableStateFlow<Int?>(null)

    val currentUserId: StateFlow<Int?> =
        _currentUserId.asStateFlow()


    // ==========================================
    // 2. ESTABLECER USUARIO ACTIVO
    // ==========================================

    fun setUserId(userId: Int) {

        if (_currentUserId.value != userId) {

            _selectedFilter.value = TaskFilter.ALL

            _currentUserId.value = userId

        }

    }


    // ==========================================
    // 3. CERRAR SESIÓN DEL USUARIO
    // ==========================================

    fun clearUser() {

        _currentUserId.value = null

        _selectedFilter.value = TaskFilter.ALL

    }


    // ==========================================
    // 4. LISTA DE TAREAS POR USUARIO
    // ==========================================

    val tasks: StateFlow<List<Task>> =

        _currentUserId.flatMapLatest { userId ->

            if (userId == null) {

                flowOf(emptyList())

            } else {

                repository.observeTasks(userId)
                    .onStart {
                        emit(emptyList())
                    }

            }

        }.stateIn(

            scope = viewModelScope,

            started = SharingStarted.WhileSubscribed(5_000),

            initialValue = emptyList()

        )


    // ==========================================
    // 5. FILTRO SELECCIONADO
    // ==========================================

    private val _selectedFilter =
        MutableStateFlow(TaskFilter.ALL)

    val selectedFilter: StateFlow<TaskFilter> =
        _selectedFilter.asStateFlow()


    // ==========================================
    // 6. CAMBIAR FILTRO
    // ==========================================

    fun changeFilter(filter: TaskFilter) {

        _selectedFilter.value = filter

    }


    // ==========================================
    // 7. COMPLETAR O REABRIR TAREA
    // ==========================================

    fun toggleTask(taskId: Int) {

        val userId = _currentUserId.value ?: return

        viewModelScope.launch {

            repository.toggleTask(

                userId = userId,

                taskId = taskId

            )

        }

    }


    // ==========================================
    // 8. ELIMINAR TAREA
    // ==========================================

    fun deleteTask(taskId: Int) {

        val userId = _currentUserId.value ?: return

        viewModelScope.launch {

            repository.deleteTask(

                userId = userId,

                taskId = taskId

            )

        }

    }


    // ==========================================
    // 9. AGREGAR TAREA
    // ==========================================

    fun addTask(

        title: String,

        priority: TaskPriority,

        category: String = "Personal"

    ) {

        val userId = _currentUserId.value ?: return

        viewModelScope.launch {

            repository.addTask(

                userId = userId,

                title = title,

                priority = priority,

                category = category

            )

        }

    }


    // ==========================================
    // 10. EDITAR TAREA
    // ==========================================

    fun updateTask(

        taskId: Int,

        title: String,

        category: String,

        priority: TaskPriority

    ) {

        val userId = _currentUserId.value ?: return

        viewModelScope.launch {

            repository.updateTask(

                userId = userId,

                taskId = taskId,

                title = title,

                category = category,

                priority = priority

            )

        }

    }


    // ==========================================
    // 11. ELIMINAR TODAS LAS TAREAS DEL USUARIO
    // ==========================================

    fun deleteAllTasks() {

        val userId = _currentUserId.value ?: return

        viewModelScope.launch {

            repository.deleteAllTasks(userId)

        }

    }

}


// ==============================================
// VIEWMODEL FACTORY
// ==============================================

class TaskViewModelFactory(

    private val repository: TaskRepository

) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(

        modelClass: Class<T>

    ): T {

        if (

            modelClass.isAssignableFrom(
                TaskViewModel::class.java
            )

        ) {

            @Suppress("UNCHECKED_CAST")

            return TaskViewModel(repository) as T

        }


        throw IllegalArgumentException(

            "Unknown ViewModel class: ${modelClass.name}"

        )

    }

}
