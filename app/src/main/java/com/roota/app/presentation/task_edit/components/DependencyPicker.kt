package com.roota.app.presentation.task_edit.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.roota.app.domain.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DependencyPicker(
    availableTasks: List<Task>,           // Реальный список задач проекта
    selectedParentIds: List<Long>,
    onParentSelected: (Long) -> Unit,
    onParentRemoved: (Long) -> Unit
) {
    Column {
        Text(
            text = "Зависит от (родительские задачи)",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (availableTasks.isEmpty()) {
            Text(
                text = "Другие задачи в проекте пока отсутствуют",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        LazyColumn(
            modifier = Modifier.height(240.dp)
        ) {
            items(availableTasks) { task ->
                val isSelected = selectedParentIds.contains(task.id)

                ListItem(
                    headlineContent = { Text(task.title) },
                    supportingContent = { Text("ID: ${task.id}") },
                    leadingContent = {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = {
                                if (it) onParentSelected(task.id)
                                else onParentRemoved(task.id)
                            }
                        )
                    },
                    modifier = Modifier.clickable {
                        if (isSelected) onParentRemoved(task.id)
                        else onParentSelected(task.id)
                    }
                )
            }
        }
    }
}