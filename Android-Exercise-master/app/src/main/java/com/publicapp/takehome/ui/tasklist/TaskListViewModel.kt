package com.publicapp.takehome.ui.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.publicapp.takehome.R
import com.publicapp.takehome.data.model.Task
import com.publicapp.takehome.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val repo: TaskRepository
) : ViewModel() {

    val uiState: StateFlow<TaskListUiState> =
        repo.observeTasks()
            .map { tasks ->
                val sorted = tasks.sortedWith(
                    compareBy<Task> { it.isCompleted }
                        .thenByDescending { it.createdAt }
                )
                if (sorted.isEmpty()) TaskListUiState.Empty else TaskListUiState.Content(sorted)
            }
            .onStart { emit(TaskListUiState.Loading) }
            .catch { emit(TaskListUiState.Error(R.string.error_load_failed)) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TaskListUiState.Loading
            )

    fun onToggleCompleted(taskId: String) {
        viewModelScope.launch {
            repo.toggleCompleted(taskId)
        }
    }
}
