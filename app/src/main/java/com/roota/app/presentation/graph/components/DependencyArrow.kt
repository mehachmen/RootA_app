package com.roota.app.presentation.graph.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

fun DrawScope.drawDependencyArrow(
    start: Offset,
    end: Offset,
    scale: Float
) {
    val arrowColor = Color(0xFF448AFF)
    val strokeWidth = 3.5f * scale

    // Основная линия
    drawLine(
        color = arrowColor,
        start = start,
        end = end,
        strokeWidth = strokeWidth
    )

    // Стрелка
    val arrowSize = 12f * scale
    val direction = (end - start).normalize()
    val perpendicular = Offset(-direction.y, direction.x)

    val arrowPoint1 = end - direction * arrowSize + perpendicular * (arrowSize * 0.4f)
    val arrowPoint2 = end - direction * arrowSize - perpendicular * (arrowSize * 0.4f)

    val path = Path().apply {
        moveTo(end.x, end.y)
        lineTo(arrowPoint1.x, arrowPoint1.y)
        lineTo(arrowPoint2.x, arrowPoint2.y)
        close()
    }

    drawPath(path = path, color = arrowColor)
}

private fun Offset.normalize(): Offset {
    val length = this.getDistance()
    return if (length > 0f) this / length else Offset.Zero
}