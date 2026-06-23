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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.roota.app.domain.model.TaskPriority
import com.roota.app.presentation.ui.theme.ButtonShape
import com.roota.app.presentation.ui.theme.DarkSurface
import com.roota.app.presentation.ui.theme.PriorityHigh
import com.roota.app.presentation.ui.theme.PriorityLow
import com.roota.app.presentation.ui.theme.PriorityMedium
import com.roota.app.presentation.ui.theme.TextSecondary
import com.roota.app.presentation.ui.util.priorityLabel

@Composable
fun PrioritySegmentedControl(
    selected: TaskPriority,
    onSelected: (TaskPriority) -> Unit,
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
        TaskPriority.entries.forEach { priority ->
            val isSelected = priority == selected
            val accent = priorityAccentColor(priority)
            Text(
                text = priorityLabel(priority),
                modifier = Modifier
                    .weight(1f)
                    .clip(ButtonShape)
                    .background(if (isSelected) accent.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { onSelected(priority) }
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

fun priorityAccentColor(priority: TaskPriority): Color = when (priority) {
    TaskPriority.LOW -> PriorityLow
    TaskPriority.MEDIUM -> PriorityMedium
    TaskPriority.HIGH -> PriorityHigh
}
