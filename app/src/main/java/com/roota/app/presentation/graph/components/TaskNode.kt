package com.roota.app.presentation.graph.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.sp
import com.roota.app.domain.model.TaskStatus
import com.roota.app.presentation.graph.model.TaskUi

fun DrawScope.drawTaskNode(
    task: TaskUi,
    scale: Float,
    offset: Offset,
    textMeasurer: TextMeasurer
) {
    val left = task.posX * scale + offset.x - 70f * scale
    val top = task.posY * scale + offset.y - 35f * scale
    val width = 140f * scale
    val height = 70f * scale

    val nodeColor = when (task.status) {
        TaskStatus.COMPLETED -> Color(0xFF00E676)
        TaskStatus.IN_PROGRESS -> Color(0xFFFFC107)
        else -> Color(0xFF1E1E1E)
    }

    // Основной прямоугольник
    drawRect(
        color = nodeColor,
        topLeft = Offset(left, top),
        size = androidx.compose.ui.geometry.Size(width, height)
    )

    // Белая обводка
    drawRect(
        color = Color.White,
        topLeft = Offset(left, top),
        size = androidx.compose.ui.geometry.Size(width, height),
        style = Stroke(width = 4f)
    )

    // Название
    drawText(
        textMeasurer = textMeasurer,
        text = task.title,
        topLeft = Offset(left + 12f * scale, top + 24f * scale),
        style = TextStyle(
            color = Color.White,
            fontSize = 13.sp * scale
        )
    )
}