package com.publicapp.takehome.ui.tasklist

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.publicapp.takehome.R
import com.publicapp.takehome.data.model.Task
import com.publicapp.takehome.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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

    private val _events = MutableSharedFlow<TaskListEvent>()
    val events: SharedFlow<TaskListEvent> = _events

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

    fun onDelete(task: Task) {
        viewModelScope.launch {
            val result = repo.deleteTask(task.id)
            if (result.isSuccess) {
                _events.emit(TaskListEvent.ShowUndo(task, R.string.task_deleted))
            } else {
                _events.emit(TaskListEvent.ShowError(R.string.error_delete_failed))
            }
        }
    }

    fun onUndoDelete(task: Task) {
        viewModelScope.launch {
            repo.restoreTask(task)
        }
    }


}

sealed interface TaskListEvent {
    data class ShowUndo(val deletedTask: Task, @StringRes val messageResId: Int) : TaskListEvent
    data class ShowError(@StringRes val messageResId: Int) : TaskListEvent
}

