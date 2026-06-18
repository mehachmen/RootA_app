package com.roota.app.presentation.graph.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.roota.app.presentation.ui.theme.AccentBlue
import com.roota.app.presentation.ui.theme.AccentGreen

fun DrawScope.drawDependencyArrow(
    start: Offset,
    end: Offset,
    scale: Float,
    sourceCompleted: Boolean = false,
    linkColor: Color = AccentBlue
) {
    val arrowColor = if (sourceCompleted) AccentGreen else linkColor
    val glowColor = arrowColor.copy(alpha = 0.35f)
    val strokeWidth = 3f * scale.coerceAtLeast(0.5f)

    val direction = (end - start)
    val length = direction.getDistance()
    if (length < 1f) return

    val normalized = direction / length
    val nodePadding = 20f * scale
    val adjustedStart = start + normalized * nodePadding
    val adjustedEnd = end - normalized * nodePadding

    drawLine(
        color = glowColor,
        start = adjustedStart,
        end = adjustedEnd,
        strokeWidth = strokeWidth * 3f
    )

    drawLine(
        color = arrowColor,
        start = adjustedStart,
        end = adjustedEnd,
        strokeWidth = strokeWidth
    )

    val arrowSize = 10f * scale.coerceAtLeast(0.5f)
    val perpendicular = Offset(-normalized.y, normalized.x)

    val arrowPoint1 = adjustedEnd - normalized * arrowSize + perpendicular * (arrowSize * 0.45f)
    val arrowPoint2 = adjustedEnd - normalized * arrowSize - perpendicular * (arrowSize * 0.45f)

    val path = Path().apply {
        moveTo(adjustedEnd.x, adjustedEnd.y)
        lineTo(arrowPoint1.x, arrowPoint1.y)
        lineTo(arrowPoint2.x, arrowPoint2.y)
        close()
    }

    drawPath(path = path, color = glowColor)
    drawPath(path = path, color = arrowColor)
}
