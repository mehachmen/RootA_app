package com.roota.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.roota.app.R
import com.roota.app.presentation.ui.theme.CardShape
import com.roota.app.presentation.ui.theme.DarkSurface
import com.roota.app.presentation.ui.theme.TextSecondary

data class LinkColorOption(
    val argb: Long,
    val label: String
)

val DefaultLinkColors = listOf(
    LinkColorOption(0xFF448AFFFFL, "Синий"),
    LinkColorOption(0xFF00E676FFL, "Зелёный"),
    LinkColorOption(0xFFB388FFFFL, "Фиолетовый"),
    LinkColorOption(0xFFFF9100FFL, "Оранжевый"),
    LinkColorOption(0xFFFF4081FFL, "Розовый"),
)

@Composable
fun LinkColorPicker(
    selectedArgb: Long,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    colors: List<LinkColorOption> = DefaultLinkColors
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.create_project_link_color),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            colors.forEach { option ->
                val color = Color(option.argb)
                val isSelected = option.argb == selectedArgb
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.25f))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) color else TextSecondary.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(option.argb) },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(DarkSurface)
        ) {
            content()
        }
    }
}
