package com.publicapp.takehome.ui.addtask


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.publicapp.takehome.R
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddTaskRoute(
    onBack: () -> Unit,
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AddTaskEvent.NavigateBack -> onBack()

                is AddTaskEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = context.resources.getString(event.messageResId)
                    )
                }
            }
        }
    }



    AddTaskScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onSaveClick = viewModel::onSaveClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskScreen(
    state: AddTaskUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.add_task_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = topAppBarColors()
            )

        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.title_label)) },
                enabled = !state.isSaving,
                isError = state.titleErrorResId != null,
                supportingText = {
                    val counter = stringResource(
                        R.string.title_counter,
                        state.title.length
                    )

                    val error = state.titleErrorResId?.let {
                        stringResource(R.string.error_bullet, it)
                    } ?: ""

                    Text(counter + error)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text(stringResource(R.string.description_label)) },
                enabled = !state.isSaving,
                isError = state.descriptionErrorResId != null,
                supportingText = {
                    val counter = stringResource(
                        R.string.description_counter,
                        state.description.length
                    )

                    val error = state.descriptionErrorResId?.let {
                        stringResource(R.string.error_bullet, it)
                    } ?: ""

                    Text(counter + error)
                },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(Modifier.weight(1f))

            Button(
                onClick = onSaveClick,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(R.string.saving))
                } else {
                    Text(stringResource(R.string.save))
                }
            }

        }
    }
}
