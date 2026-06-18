package com.roota.app.presentation.task_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.roota.app.R
import com.roota.app.presentation.task_edit.components.DependencyPicker
import com.roota.app.presentation.ui.components.DeadlineTextField
import com.roota.app.presentation.ui.components.PrioritySegmentedControl
import com.roota.app.presentation.ui.components.RootATextField
import com.roota.app.presentation.ui.components.RootATopBar
import com.roota.app.presentation.ui.components.StatusSegmentedControl
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.ButtonShape
import com.roota.app.presentation.ui.theme.DarkBackground
import com.roota.app.presentation.ui.theme.Dimens
import com.roota.app.presentation.ui.theme.TagRed
import com.roota.app.presentation.ui.theme.TextSecondary
import com.roota.app.presentation.ui.util.DateFormatters

@Composable
fun TaskEditScreen(
    projectId: Long,
    taskId: Long? = null,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    navController: NavController? = null,
    viewModel: TaskEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val pickedParentsFlow = remember(navController) {
        navController?.currentBackStackEntry?.savedStateHandle
            ?.getStateFlow<List<Long>?>("picked_parents", null)
    }
    val pickedParents by pickedParentsFlow?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(taskId) {
        viewModel.loadTaskForEdit(taskId, projectId)
    }

    LaunchedEffect(pickedParents) {
        pickedParents?.let { ids ->
            viewModel.setSelectedParentIds(ids)
            navController?.currentBackStackEntry?.savedStateHandle
                ?.remove<List<Long>>("picked_parents")
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    val titleRes = if (state.isNewTask) R.string.task_edit_new else R.string.task_edit_existing
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RootATopBar(
                title = stringResource(titleRes),
                onBackClick = onCancel,
                actions = {
                    if (!state.isNewTask) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.task_edit_delete),
                                tint = TagRed
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading && state.task == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground)
                    .padding(padding)
                    .padding(Dimens.screenPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                state.task?.let { task ->
                    RootATextField(
                        value = task.title,
                        onValueChange = viewModel::updateTitle,
                        label = stringResource(R.string.task_edit_name_label)
                    )

                    Spacer(modifier = Modifier.height(Dimens.itemSpacing))

                    RootATextField(
                        value = task.description ?: "",
                        onValueChange = viewModel::updateDescription,
                        label = stringResource(R.string.task_edit_desc_label),
                        minLines = 4
                    )

                    Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

                    Text(
                        text = stringResource(R.string.task_edit_priority),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PrioritySegmentedControl(
                        selected = task.priority,
                        onSelected = viewModel::updatePriority
                    )

                    Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

                    Text(
                        text = stringResource(R.string.task_edit_deadline),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DeadlineTextField(
                        value = state.deadlineInput,
                        onValueChange = viewModel::updateDeadlineInput,
                        label = stringResource(R.string.task_edit_deadline_hint),
                        isError = state.deadlineInput.isNotBlank() &&
                            DateFormatters.validateDeadlineInput(state.deadlineInput) != null,
                        supportingText = state.deadlineInput.takeIf { it.isNotBlank() }
                            ?.let { DateFormatters.validateDeadlineInput(it) }
                    )

                    Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

                    Text(
                        text = stringResource(R.string.task_edit_status),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusSegmentedControl(
                        selected = task.status,
                        onSelected = viewModel::updateStatus
                    )

                    Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

                    DependencyPicker(
                        availableTasks = state.availableTasks,
                        selectedParentIds = state.selectedParentIds,
                        onParentSelected = viewModel::addParentDependency,
                        onParentRemoved = viewModel::removeParentDependency,
                        onPickOnCanvas = {
                            navController?.currentBackStackEntry?.savedStateHandle?.set(
                                "pick_initial_parents",
                                state.selectedParentIds
                            )
                            val pickForTask = if (state.isNewTask) 0L else task.id
                            navController?.navigate(
                                "graph/$projectId?pickForTask=$pickForTask"
                            )
                        },
                        showCanvasPicker = navController != null
                    )

                    Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

                    Button(
                        onClick = { viewModel.saveTask(projectId, onSuccess = onSave) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.buttonHeight),
                        enabled = task.title.isNotBlank() && !state.isLoading,
                        shape = ButtonShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGreen,
                            contentColor = Color.Black,
                            disabledContainerColor = AccentGreen.copy(alpha = 0.3f)
                        )
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.task_edit_save),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.task_edit_delete)) },
            text = { Text(stringResource(R.string.delete_task_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteTask(onSuccess = onSave)
                    }
                ) {
                    Text(stringResource(R.string.delete), color = TagRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = DarkBackground
        )
    }
}
