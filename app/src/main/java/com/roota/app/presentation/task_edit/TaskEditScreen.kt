package com.roota.app.presentation.task_edit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.roota.app.domain.model.TaskStatus
import com.roota.app.presentation.task_edit.components.DependencyPicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    projectId: Long,
    taskId: Long? = null,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    viewModel: TaskEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(taskId) {
        viewModel.loadTaskForEdit(taskId, projectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isNewTask) "Новая задача" else "Редактировать задачу") },
                navigationIcon = {
                    IconButton(onClick = onCancel) { Text("Отмена") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            state.task?.let { task ->
                OutlinedTextField(
                    value = task.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Название задачи") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = task.description ?: "",
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Статус", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskStatus.values().forEach { status ->
                        FilterChip(
                            selected = task.status == status,
                            onClick = { viewModel.updateStatus(status) },
                            label = { Text(status.name.replace("_", " ")) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // DependencyPicker с реальными данными
                DependencyPicker(
                    availableTasks = state.availableTasks,
                    selectedParentIds = state.selectedParentIds,
                    onParentSelected = { viewModel.addParentDependency(it) },
                    onParentRemoved = { viewModel.removeParentDependency(it) }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        viewModel.saveTask(projectId)
                        onSave()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = task.title.isNotBlank()
                ) {
                    Text("Сохранить задачу")
                }
            }
        }
    }
}