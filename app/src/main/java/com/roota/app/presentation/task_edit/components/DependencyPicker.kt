package com.roota.app.presentation.task_edit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.roota.app.R
import com.roota.app.domain.model.Task
import com.roota.app.presentation.ui.components.statusLabel
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.CardShape
import com.roota.app.presentation.ui.theme.DarkSurface
import com.roota.app.presentation.ui.theme.TextPrimary
import com.roota.app.presentation.ui.theme.TextSecondary

@Composable
fun DependencyPicker(
    availableTasks: List<Task>,
    selectedParentIds: List<Long>,
    onParentSelected: (Long) -> Unit,
    onParentRemoved: (Long) -> Unit,
    onPickOnCanvas: () -> Unit = {},
    showCanvasPicker: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.task_edit_deps),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            if (showCanvasPicker && availableTasks.isNotEmpty()) {
                TextButton(onClick = onPickOnCanvas) {
                    Text(
                        text = stringResource(R.string.task_edit_pick_canvas),
                        color = AccentGreen
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (availableTasks.isEmpty()) {
            Text(
                text = stringResource(R.string.task_edit_deps_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(CardShape)
                .background(DarkSurface)
                .border(
                    width = 1.dp,
                    color = TextSecondary.copy(alpha = 0.2f),
                    shape = CardShape
                )
        ) {
            LazyColumn(modifier = Modifier.padding(4.dp)) {
                items(availableTasks, key = { it.id }) { task ->
                    val isSelected = selectedParentIds.contains(task.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                if (isSelected) onParentRemoved(task.id)
                                else onParentSelected(task.id)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = {
                                if (it) onParentSelected(task.id)
                                else onParentRemoved(task.id)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AccentGreen,
                                checkmarkColor = DarkSurface
                            )
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = statusLabel(task.status),
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
