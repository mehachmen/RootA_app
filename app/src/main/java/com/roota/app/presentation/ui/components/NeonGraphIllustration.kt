package com.roota.app.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.roota.app.presentation.ui.theme.AccentBlue
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.InProgressYellow

@Composable
fun NeonGraphIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        val w = size.width
        val h = size.height
        val nodes = listOf(
            Triple(w * 0.2f, h * 0.35f, AccentGreen),
            Triple(w * 0.5f, h * 0.2f, AccentBlue),
            Triple(w * 0.8f, h * 0.4f, InProgressYellow),
            Triple(w * 0.35f, h * 0.7f, AccentBlue),
            Triple(w * 0.65f, h * 0.75f, AccentGreen),
        )
        val edges = listOf(0 to 1, 1 to 2, 0 to 3, 1 to 3, 3 to 4, 2 to 4)

        edges.forEach { (from, to) ->
            val start = Offset(nodes[from].first, nodes[from].second)
            val end = Offset(nodes[to].first, nodes[to].second)
            drawLine(
                color = AccentBlue.copy(alpha = 0.25f),
                start = start,
                end = end,
                strokeWidth = 8f
            )
            drawLine(
                color = AccentBlue.copy(alpha = 0.7f),
                start = start,
                end = end,
                strokeWidth = 2.5f
            )
        }

        nodes.forEach { (x, y, color) ->
            drawCircle(
                color = color.copy(alpha = 0.2f),
                radius = 28f,
                center = Offset(x, y)
            )
            drawCircle(
                color = color,
                radius = 18f,
                center = Offset(x, y),
                style = Stroke(width = 2.5f)
            )
            drawCircle(
                color = Color(0xFF1E1E1E),
                radius = 14f,
                center = Offset(x, y)
            )
        }
    }
}
