package com.roota.app.presentation.task_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.roota.app.R
import com.roota.app.domain.model.Task
import com.roota.app.presentation.ui.components.statusAccentColor
import com.roota.app.presentation.ui.components.statusLabel
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.BottomSheetShape
import com.roota.app.presentation.ui.theme.ButtonShape
import com.roota.app.presentation.ui.theme.DarkSurface
import com.roota.app.presentation.ui.theme.Dimens
import com.roota.app.presentation.ui.theme.TextPrimary
import com.roota.app.presentation.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailSheet(
    taskId: Long,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit = {},
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    LaunchedEffect(state.error) {
        state.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = DarkSurface,
        shape = BottomSheetShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.screenPadding)
                .padding(bottom = Dimens.screenPadding)
        ) {
            Text(
                text = stringResource(R.string.task_detail_title),
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

            if (state.isLoading) {
                CircularProgressIndicator(color = AccentGreen)
            } else {
                state.task?.let { task ->
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )

                    task.description?.takeIf { it.isNotBlank() }?.let { desc ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetaBlock(
                            label = stringResource(R.string.task_detail_priority),
                            value = com.roota.app.presentation.ui.util.priorityLabel(task.priority),
                            valueColor = com.roota.app.presentation.ui.components.priorityAccentColor(task.priority),
                            modifier = Modifier.weight(1f)
                        )
                        MetaBlock(
                            label = stringResource(R.string.task_detail_deadline),
                            value = com.roota.app.presentation.ui.util.DateFormatters
                                .formatDeadlineDisplay(task.deadline),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

                    Text(
                        text = stringResource(R.string.task_detail_status),
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusBadge(task = task)

                    Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

                    Text(
                        text = stringResource(R.string.task_detail_dependencies),
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.parentTasks.isEmpty()) {
                        Text(
                            text = stringResource(R.string.task_detail_no_dependencies),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.parentTasks.forEach { parent ->
                                DependencyItem(task = parent)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

                    Button(
                        onClick = onEditClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.buttonHeight),
                        shape = ButtonShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGreen,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.task_detail_edit),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } ?: Text(
                    text = stringResource(R.string.task_not_found),
                    color = TextSecondary
                )
            }

            SnackbarHost(snackbarHostState)
        }
    }
}

@Composable
private fun MetaBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF2A2A2A))
            .padding(12.dp)
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatusBadge(task: Task) {
    val accent = statusAccentColor(task.status)
    Text(
        text = statusLabel(task.status),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(accent.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        style = MaterialTheme.typography.labelLarge,
        color = accent
    )
}

@Composable
private fun DependencyItem(task: Task) {
    val accent = statusAccentColor(task.status)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF2A2A2A))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(accent)
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
                color = accent
            )
        }
    }
}
