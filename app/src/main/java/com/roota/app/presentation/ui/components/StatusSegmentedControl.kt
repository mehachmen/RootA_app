package com.roota.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.roota.app.R
import com.roota.app.domain.model.TaskStatus
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.ButtonShape
import com.roota.app.presentation.ui.theme.DarkSurface
import com.roota.app.presentation.ui.theme.InProgressYellow
import com.roota.app.presentation.ui.theme.TextPrimary
import com.roota.app.presentation.ui.theme.TextSecondary

@Composable
fun StatusSegmentedControl(
    selected: TaskStatus,
    onSelected: (TaskStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ButtonShape)
            .background(DarkSurface)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TaskStatus.entries.forEach { status ->
            val isSelected = status == selected
            val accent = statusAccentColor(status)
            Text(
                text = statusLabel(status),
                modifier = Modifier
                    .weight(1f)
                    .clip(ButtonShape)
                    .background(if (isSelected) accent.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { onSelected(status) }
                    .padding(vertical = 12.dp),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) accent else TextSecondary
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun statusLabel(status: TaskStatus): String = when (status) {
    TaskStatus.NOT_STARTED -> stringResource(R.string.status_not_started)
    TaskStatus.IN_PROGRESS -> stringResource(R.string.status_in_progress)
    TaskStatus.COMPLETED -> stringResource(R.string.status_completed)
}

fun statusAccentColor(status: TaskStatus): Color = when (status) {
    TaskStatus.NOT_STARTED -> TextSecondary
    TaskStatus.IN_PROGRESS -> InProgressYellow
    TaskStatus.COMPLETED -> AccentGreen
}
