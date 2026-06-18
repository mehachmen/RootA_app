package com.roota.app.presentation.graph.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer
import com.roota.app.presentation.graph.model.GraphState
import com.roota.app.presentation.graph.model.TaskUi

@Composable
fun GraphCanvas(
    state: GraphState,
    onDrag: (Offset) -> Unit,
    onScale: (Float) -> Unit,
    onTaskClick: (Long) -> Unit,
    onTaskLongClick: (Long) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(state) {
                // Zoom
                detectTransformGestures { _, _, zoom, _ ->
                    onScale(zoom)
                }

                // Drag холста
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount)
                }

                // ⬇️ ИСПРАВЛЕННЫЙ БЛОК
                detectTapGestures(
                    onTap = { tapOffset ->
                        findTaskAt(tapOffset, state)?.let { task ->
                            onTaskClick(task.id)
                        }
                    },
                    onLongPress = { longPressOffset ->  // ⬅️ ВОТ ТАК!
                        findTaskAt(longPressOffset, state)?.let { task ->
                            onTaskLongClick(task.id)
                        }
                    }
                )
            }
    ) {
        val scale = state.scale
        val offset = state.offset
        val gridSize = 40f * scale

        // Dot Grid
        for (x in (-offset.x % gridSize).toInt()..size.width.toInt() step gridSize.toInt()) {
            drawLine(Color(0xFF2A2A2A), Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height))
        }
        for (y in (-offset.y % gridSize).toInt()..size.height.toInt() step gridSize.toInt()) {
            drawLine(Color(0xFF2A2A2A), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()))
        }

        // Dependencies
        state.dependencies.forEach { dep ->
            drawDependencyArrow(start = dep.start, end = dep.end, scale = scale)
        }

        // Tasks
        state.tasks.forEach { task ->
            drawTaskNode(
                task = task,
                scale = scale,
                offset = offset,
                textMeasurer = textMeasurer
            )
        }
    }
}

// Вспомогательная функция
private fun findTaskAt(offset: Offset, state: GraphState): TaskUi? {
    state.tasks.forEach { task ->
        val left = task.posX * state.scale + state.offset.x - 70f * state.scale
        val top = task.posY * state.scale + state.offset.y - 35f * state.scale
        val right = left + 140f * state.scale
        val bottom = top + 70f * state.scale

        if (offset.x in left..right && offset.y in top..bottom) {
            return task
        }
    }
    return null
}