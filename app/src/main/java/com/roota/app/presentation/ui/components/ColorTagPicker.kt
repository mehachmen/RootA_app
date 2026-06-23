package com.roota.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
import com.roota.app.presentation.ui.theme.TagAmberArgb
import com.roota.app.presentation.ui.theme.TagCyanArgb
import com.roota.app.presentation.ui.theme.TagNeonGreenArgb
import com.roota.app.presentation.ui.theme.TagPinkArgb
import com.roota.app.presentation.ui.theme.TagPurpleArgb
import com.roota.app.presentation.ui.theme.TagRedArgb
import com.roota.app.presentation.ui.theme.TextPrimary
import com.roota.app.presentation.ui.theme.TextSecondary

data class ColorTagOption(
    val argb: Long,
    val label: String
)

val DefaultColorTags = listOf(
    ColorTagOption(TagNeonGreenArgb, "Неоновый зелёный"),
    ColorTagOption(TagCyanArgb, "Голубой"),
    ColorTagOption(TagPurpleArgb, "Фиолетовый"),
    ColorTagOption(TagAmberArgb, "Оранжевый"),
    ColorTagOption(TagRedArgb, "Красный"),
    ColorTagOption(TagPinkArgb, "Розовый"),
)

@Composable
fun ColorTagPicker(
    selectedArgb: Long,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    colors: List<ColorTagOption> = DefaultColorTags
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.create_project_color_tag),
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
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
                        .background(color.copy(alpha = if (isSelected) 0.35f else 0.2f))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) color else TextSecondary.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(option.argb) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
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
}

@Composable
fun ProjectPreviewCard(
    title: String,
    description: String,
    colorTagArgb: Long,
    modifier: Modifier = Modifier
) {
    val accent = Color(colorTagArgb)
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.create_project_preview),
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(DarkSurface)
                .border(
                    width = 1.dp,
                    color = accent.copy(alpha = 0.5f),
                    shape = CardShape
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(accent)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title.ifBlank { stringResource(R.string.create_project_preview_title) },
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description.ifBlank { stringResource(R.string.create_project_preview_desc) },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
