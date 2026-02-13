package com.publicapp.takehome.ui.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.publicapp.takehome.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddTaskUiState(
    val title: String = "",
    val description: String = "",
    val isSaving: Boolean = false,
    val titleError: String? = null,
    val descriptionError: String? = null
)

sealed interface AddTaskEvent {
    data object NavigateBack : AddTaskEvent
    data class ShowError(val message: String) : AddTaskEvent
}

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val repo: TaskRepository
) : ViewModel() {

    val uiState = MutableStateFlow(AddTaskUiState())

    private val _events = MutableSharedFlow<AddTaskEvent>()
    val events: SharedFlow<AddTaskEvent> = _events

    fun onTitleChange(value: String) {
        uiState.update { it.copy(title = value.take(50), titleError = null) }
    }

    fun onDescriptionChange(value: String) {
        uiState.update { it.copy(description = value.take(200), descriptionError = null) }
    }

    fun onSaveClick() {
        val title = uiState.value.title.trim()
        val desc = uiState.value.description.trim()

        val titleError = if (title.isBlank()) "Requerido" else null
        val descError = if (desc.isBlank()) "Requerido" else null

        if (titleError != null || descError != null) {
            uiState.update { it.copy(titleError = titleError, descriptionError = descError) }
            return
        }

        viewModelScope.launch {
            uiState.update { it.copy(isSaving = true) }

            val result = repo.addTask(title, desc)

            uiState.update { it.copy(isSaving = false) }

            if (result.isSuccess) {
                _events.emit(AddTaskEvent.NavigateBack)
            } else {
                _events.emit(AddTaskEvent.ShowError("No se pudo guardar. Reintentá."))
            }
        }
    }
}
