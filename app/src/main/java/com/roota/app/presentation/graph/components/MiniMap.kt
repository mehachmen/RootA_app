package com.roota.app.presentation.graph.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.roota.app.presentation.graph.model.GraphState
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.DarkSurface
import com.roota.app.presentation.ui.theme.GridColor

@Composable
fun MiniMap(
    state: GraphState,
    canvasSize: Size,
    modifier: Modifier = Modifier,
    onNavigate: (Offset) -> Unit = {}
) {
    if (state.tasks.isEmpty()) return

    val mapWidth = 120.dp
    val mapHeight = 80.dp

    Column(
        modifier = modifier
            .width(mapWidth)
            .background(DarkSurface.copy(alpha = 0.92f), RoundedCornerShape(8.dp))
            .border(1.dp, GridColor, RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        Text(
            text = "Карта",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(mapHeight)
        ) {
            val tasks = state.tasks
            val minX = tasks.minOf { it.posX } - 80f
            val maxX = tasks.maxOf { it.posX } + 80f
            val minY = tasks.minOf { it.posY } - 60f
            val maxY = tasks.maxOf { it.posY } + 60f
            val graphW = (maxX - minX).coerceAtLeast(1f)
            val graphH = (maxY - minY).coerceAtLeast(1f)

            fun toMini(graphX: Float, graphY: Float): Offset {
                val x = ((graphX - minX) / graphW) * size.width
                val y = ((graphY - minY) / graphH) * size.height
                return Offset(x, y)
            }

            state.dependencies.forEach { dep ->
                val source = tasks.find { it.id == dep.sourceTaskId } ?: return@forEach
                val target = tasks.find { it.id == dep.targetTaskId } ?: return@forEach
                val start = toMini(source.posX, source.posY)
                val end = toMini(target.posX, target.posY)
                drawLine(
                    color = GridColor,
                    start = start,
                    end = end,
                    strokeWidth = 1f
                )
            }

            tasks.forEach { task ->
                val center = toMini(task.posX, task.posY)
                drawCircle(
                    color = AccentGreen.copy(alpha = 0.8f),
                    radius = 3f,
                    center = center
                )
            }

            if (canvasSize.width > 0 && canvasSize.height > 0) {
                val viewLeft = (-state.offset.x / state.scale - minX) / graphW * size.width
                val viewTop = (-state.offset.y / state.scale - minY) / graphH * size.height
                val viewW = (canvasSize.width / state.scale / graphW) * size.width
                val viewH = (canvasSize.height / state.scale / graphH) * size.height

                drawRect(
                    color = AccentGreen.copy(alpha = 0.15f),
                    topLeft = Offset(viewLeft.coerceIn(0f, size.width), viewTop.coerceIn(0f, size.height)),
                    size = Size(
                        viewW.coerceIn(8f, size.width),
                        viewH.coerceIn(8f, size.height)
                    ),
                    style = Stroke(width = 1.5f)
                )
            }
        }
    }
}
