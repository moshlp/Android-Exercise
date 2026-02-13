package com.publicapp.takehome.ui.tasklist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.publicapp.takehome.R
import com.publicapp.takehome.data.model.Task

@Composable
fun TaskListRoute(
    onAddClick: () -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    TaskListScreen(
        state = state,
        onAddClick = onAddClick,
        onToggleCompleted = viewModel::onToggleCompleted
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskListScreen(
    state: TaskListUiState,
    onAddClick: () -> Unit,
    onToggleCompleted: (String) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.task_list_title)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add_task)
                )
            }
        }
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
                is TaskListUiState.Content -> TaskList(tasks = state.tasks, onToggleCompleted = onToggleCompleted)
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
    onToggleCompleted: (String) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(tasks, key = { it.id }) { task ->
            TaskRow(task = task, onToggleCompleted = onToggleCompleted)
            HorizontalDivider()
        }
    }
}

@Composable
private fun TaskRow(
    task: Task,
    onToggleCompleted: (String) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
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
    }
}

