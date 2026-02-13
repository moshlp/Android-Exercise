package com.publicapp.takehome.ui.addtask

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.publicapp.takehome.R
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
    val titleErrorResId: Int? = null,
    val descriptionErrorResId: Int? = null
)

sealed interface AddTaskEvent {
    data object NavigateBack : AddTaskEvent
    data class ShowError(@StringRes val messageResId: Int) : AddTaskEvent
}

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val repo: TaskRepository
) : ViewModel() {

    val uiState = MutableStateFlow(AddTaskUiState())

    private val _events = MutableSharedFlow<AddTaskEvent>()
    val events: SharedFlow<AddTaskEvent> = _events

    fun onTitleChange(value: String) {
        uiState.update {
            it.copy(
                title = value.take(50),
                titleErrorResId = null
            )
        }
    }

    fun onDescriptionChange(value: String) {
        uiState.update {
            it.copy(
                description = value.take(200),
                descriptionErrorResId = null
            )
        }
    }

    fun onSaveClick() {
        val title = uiState.value.title.trim()
        val desc = uiState.value.description.trim()

        val requiredRes = R.string.error_required

        val titleError = if (title.isBlank()) requiredRes else null
        val descError = if (desc.isBlank()) requiredRes else null

        if (titleError != null || descError != null) {
            uiState.update {
                it.copy(
                    titleErrorResId = titleError,
                    descriptionErrorResId = descError
                )
            }
            return
        }

        viewModelScope.launch {
            uiState.update { it.copy(isSaving = true) }

            val result = repo.addTask(title, desc)

            uiState.update { it.copy(isSaving = false) }

            if (result.isSuccess) {
                _events.emit(AddTaskEvent.NavigateBack)
            } else {
                _events.emit(AddTaskEvent.ShowError(R.string.error_save_failed))
            }
        }
    }

}
