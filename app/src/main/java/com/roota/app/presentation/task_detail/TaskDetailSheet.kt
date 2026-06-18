package com.roota.app.presentation.task_detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.roota.app.domain.model.TaskStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailSheet(
    taskId: Long,
    onDismiss: () -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Детали задачи",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            state.task?.let { task ->
                OutlinedTextField(
                    value = task.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Название") },
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskStatus.values().forEach { status ->
                        FilterChip(
                            selected = task.status == status,
                            onClick = { viewModel.updateStatus(status) },
                            label = { Text(status.name) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.saveChanges()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить изменения")
                }
            }
        }
    }
}