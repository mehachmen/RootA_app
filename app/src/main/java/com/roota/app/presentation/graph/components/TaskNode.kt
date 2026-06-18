package com.roota.app.presentation.graph.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.sp
import com.roota.app.domain.model.TaskStatus
import com.roota.app.presentation.graph.model.TaskUi
import com.roota.app.presentation.ui.theme.AccentBlue
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.DarkSurface
import com.roota.app.presentation.ui.theme.InProgressYellow
import com.roota.app.presentation.ui.theme.TextPrimary

fun DrawScope.drawTaskNode(
    task: TaskUi,
    scale: Float,
    offset: Offset,
    textMeasurer: TextMeasurer,
    graphOffset: Offset = Offset.Zero,
    isSelected: Boolean = false,
    isDragging: Boolean = false
) {
    val posX = task.posX + graphOffset.x
    val posY = task.posY + graphOffset.y
    val halfW = GraphNodeMetrics.NODE_WIDTH / 2f * scale
    val halfH = GraphNodeMetrics.NODE_HEIGHT / 2f * scale
    val left = posX * scale + offset.x - halfW
    val top = posY * scale + offset.y - halfH
    val width = GraphNodeMetrics.NODE_WIDTH * scale
    val height = GraphNodeMetrics.NODE_HEIGHT * scale
    val corner = GraphNodeMetrics.CORNER_RADIUS * scale

    val borderColor = when (task.status) {
        TaskStatus.COMPLETED -> AccentGreen
        TaskStatus.IN_PROGRESS -> InProgressYellow
        TaskStatus.NOT_STARTED -> AccentBlue.copy(alpha = 0.55f)
    }

    val selectionColor = if (isSelected) AccentGreen.copy(alpha = 0.35f) else Color.Transparent

    if (isDragging) {
        drawRoundRect(
            color = borderColor.copy(alpha = 0.25f),
            topLeft = Offset(left - 4f, top - 4f),
            size = Size(width + 8f, height + 8f),
            cornerRadius = CornerRadius(corner + 4f)
        )
    }

    drawRoundRect(
        color = selectionColor,
        topLeft = Offset(left, top),
        size = Size(width, height),
        cornerRadius = CornerRadius(corner)
    )

    drawRoundRect(
        color = DarkSurface,
        topLeft = Offset(left, top),
        size = Size(width, height),
        cornerRadius = CornerRadius(corner)
    )

    drawRoundRect(
        color = borderColor,
        topLeft = Offset(left, top),
        size = Size(width, height),
        cornerRadius = CornerRadius(corner),
        style = Stroke(width = if (isSelected) 3.5f * scale else 2.5f * scale)
    )

    val statusLabel = statusLabel(task.status)
    val badgeColor = borderColor.copy(alpha = 0.25f)
    val badgeWidth = (statusLabel.length * 7f + 16f) * scale
    val badgeHeight = 18f * scale

    drawRoundRect(
        color = badgeColor,
        topLeft = Offset(left + 8f * scale, top + 8f * scale),
        size = Size(badgeWidth, badgeHeight),
        cornerRadius = CornerRadius(6f * scale)
    )

    drawText(
        textMeasurer = textMeasurer,
        text = statusLabel,
        topLeft = Offset(left + 12f * scale, top + 9f * scale),
        style = TextStyle(
            color = borderColor,
            fontSize = 9.sp * scale
        )
    )

    val titleMaxWidth = width - 16f * scale
    val title = if (task.title.length > 18) task.title.take(17) + "…" else task.title

    drawText(
        textMeasurer = textMeasurer,
        text = title,
        topLeft = Offset(left + 8f * scale, top + 32f * scale),
        style = TextStyle(
            color = TextPrimary,
            fontSize = 12.sp * scale
        )
    )
}

fun statusLabel(status: TaskStatus): String = when (status) {
    TaskStatus.NOT_STARTED -> "Не начата"
    TaskStatus.IN_PROGRESS -> "В процессе"
    TaskStatus.COMPLETED -> "Выполнена"
}

fun graphToScreen(graphPos: Offset, scale: Float, offset: Offset): Offset =
    Offset(graphPos.x * scale + offset.x, graphPos.y * scale + offset.y)

fun screenToGraph(screenPos: Offset, scale: Float, offset: Offset): Offset =
    Offset(
        (screenPos.x - offset.x) / scale,
        (screenPos.y - offset.y) / scale
    )

fun taskNodeBounds(task: TaskUi, scale: Float, offset: Offset): NodeBounds {
    val halfW = GraphNodeMetrics.NODE_WIDTH / 2f * scale
    val halfH = GraphNodeMetrics.NODE_HEIGHT / 2f * scale
    return NodeBounds(
        left = task.posX * scale + offset.x - halfW,
        top = task.posY * scale + offset.y - halfH,
        right = task.posX * scale + offset.x + halfW,
        bottom = task.posY * scale + offset.y + halfH
    )
}

data class NodeBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    fun contains(point: Offset): Boolean =
        point.x in left..right && point.y in top..bottom
}
