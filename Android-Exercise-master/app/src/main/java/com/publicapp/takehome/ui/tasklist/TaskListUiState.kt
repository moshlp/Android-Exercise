package com.publicapp.takehome.ui.tasklist

import androidx.annotation.StringRes
import com.publicapp.takehome.data.model.Task

sealed interface TaskListUiState {
    data object Loading : TaskListUiState
    data object Empty : TaskListUiState
    data class Content(val tasks: List<Task>) : TaskListUiState
    data class Error(@StringRes val messageResId: Int) : TaskListUiState
}
