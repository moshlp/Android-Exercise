package com.publicapp.takehome.ui.tasklist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.publicapp.takehome.R
import com.publicapp.takehome.data.model.Task
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TaskListRoute(
    onAddClick: () -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is TaskListEvent.ShowUndo -> {
                    val result = snackbarHostState.showSnackbar(
                        message = context.resources.getString(event.messageResId),
                        actionLabel = context.resources.getString(R.string.undo),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onUndoDelete(event.deletedTask)
                    }
                }

                is TaskListEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = context.resources.getString(event.messageResId),
                        withDismissAction = true
                    )
                }
            }
        }
    }

    TaskListScreen(
        state = state,
        onAddClick = onAddClick,
        snackbarHostState = snackbarHostState,
        onToggleCompleted = viewModel::onToggleCompleted,
        onDelete = viewModel::onDelete
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskListScreen(
    state: TaskListUiState,
    onAddClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onToggleCompleted: (String) -> Unit,
    onDelete: (Task) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.task_list_title)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add_task))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) } // <- CLAVE
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (state) {
                TaskListUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                TaskListUiState.Empty -> EmptyState(Modifier.align(Alignment.Center), onAddClick)
                is TaskListUiState.Error ->
                    Text(
                        text = stringResource(state.messageResId),
                        modifier = Modifier.align(Alignment.Center)
                    )
                is TaskListUiState.Content -> TaskList(
                    tasks = state.tasks,
                    onToggleCompleted = onToggleCompleted,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier, onAddClick: () -> Unit) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.task_list_empty))
        Spacer(Modifier.height(12.dp))
        Button(onClick = onAddClick) { Text(stringResource(R.string.task_list_add_task)) }
    }
}


@Composable
private fun TaskList(
    tasks: List<Task>,
    onToggleCompleted: (String) -> Unit,
    onDelete: (Task) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(tasks, key = { it.id }) { task ->
            TaskRow(task = task, onToggleCompleted = onToggleCompleted, onDelete = onDelete)
            HorizontalDivider()
        }
    }
}


@Composable
private fun TaskRow(
    task: Task,
    onToggleCompleted: (String) -> Unit,
    onDelete: (Task) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggleCompleted(task.id) }
        )

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(task.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(task.description, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }

        IconButton(onClick = { onDelete(task) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.cd_delete_task)
            )
        }
    }
}


