package com.roota.app.presentation.graph.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer
import com.roota.app.presentation.graph.model.GraphState
import com.roota.app.presentation.graph.model.TaskUi
import com.roota.app.presentation.ui.theme.DarkBackground
import com.roota.app.presentation.ui.theme.GridColor

@Composable
fun GraphCanvas(
    state: GraphState,
    onCanvasDrag: (Offset) -> Unit,
    onScale: (Float) -> Unit,
    onTaskClick: (Long) -> Unit,
    onTaskPositionCommitted: (Long, Float, Float) -> Unit,
    onTogglePickParent: (Long) -> Unit = {}
) {
    val textMeasurer = rememberTextMeasurer()
    var dragTaskId by remember { mutableLongStateOf(-1L) }
    var dragGraphDelta by remember { mutableStateOf(Offset.Zero) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .pointerInput(state.scale) {
                detectTransformGestures { _, pan, zoom, _ ->
                    if (zoom != 1f) onScale(zoom)
                    if (pan != Offset.Zero && dragTaskId < 0L) {
                        onCanvasDrag(pan)
                    }
                }
            }
            .pointerInput(state.tasks, state.scale, state.offset, state.isPickMode, dragTaskId) {
                var panEnabled = false
                detectDragGestures(
                    onDragStart = { offset ->
                        panEnabled = findTaskAt(offset, state, -1L, Offset.Zero) == null
                    },
                    onDrag = { change, dragAmount ->
                        if (panEnabled && dragTaskId < 0L) {
                            change.consume()
                            onCanvasDrag(dragAmount)
                        }
                    },
                    onDragEnd = { panEnabled = false },
                    onDragCancel = { panEnabled = false }
                )
            }
            .pointerInput(state.tasks, state.scale, state.offset, state.isPickMode) {
                if (state.isPickMode) return@pointerInput
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        findTaskAt(offset, state, -1L, Offset.Zero)?.let { task ->
                            dragTaskId = task.id
                            dragGraphDelta = Offset.Zero
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (dragTaskId >= 0L) {
                            dragGraphDelta += Offset(
                                dragAmount.x / state.scale,
                                dragAmount.y / state.scale
                            )
                        }
                    },
                    onDragEnd = {
                        if (dragTaskId >= 0L) {
                            val task = state.tasks.find { it.id == dragTaskId }
                            if (task != null) {
                                onTaskPositionCommitted(
                                    dragTaskId,
                                    task.posX + dragGraphDelta.x,
                                    task.posY + dragGraphDelta.y
                                )
                            }
                        }
                        dragTaskId = -1L
                        dragGraphDelta = Offset.Zero
                    },
                    onDragCancel = {
                        dragTaskId = -1L
                        dragGraphDelta = Offset.Zero
                    }
                )
            }
            .pointerInput(state.tasks, state.scale, state.offset, state.isPickMode, state.pickedParentIds) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        findTaskAt(tapOffset, state, dragTaskId, dragGraphDelta)?.let { task ->
                            if (state.isPickMode) {
                                onTogglePickParent(task.id)
                            } else {
                                onTaskClick(task.id)
                            }
                        }
                    }
                )
            }
    ) {
        val scale = state.scale
        val offset = state.offset
        val gridSpacing = GraphNodeMetrics.GRID_SPACING * scale
        val dotRadius = 1.5f * scale.coerceIn(0.5f, 2f)

        val startX = (-offset.x % gridSpacing).let { if (it < 0) it + gridSpacing else it }
        val startY = (-offset.y % gridSpacing).let { if (it < 0) it + gridSpacing else it }

        var x = startX
        while (x <= size.width) {
            var y = startY
            while (y <= size.height) {
                drawCircle(color = GridColor, radius = dotRadius, center = Offset(x, y))
                y += gridSpacing
            }
            x += gridSpacing
        }

        state.dependencies.forEach { dep ->
            val source = state.tasks.find { it.id == dep.sourceTaskId } ?: return@forEach
            val target = state.tasks.find { it.id == dep.targetTaskId } ?: return@forEach
            val sourcePos = taskDrawPosition(source, dragTaskId, dragGraphDelta)
            val targetPos = taskDrawPosition(target, dragTaskId, dragGraphDelta)
            drawDependencyArrow(
                start = graphToScreen(sourcePos, scale, offset),
                end = graphToScreen(targetPos, scale, offset),
                scale = scale,
                sourceCompleted = dep.sourceCompleted,
                linkColor = Color(state.linkColorArgb)
            )
        }

        state.tasks.forEach { task ->
            val extra = if (task.id == dragTaskId) dragGraphDelta else Offset.Zero
            drawTaskNode(
                task = task,
                scale = scale,
                offset = offset,
                textMeasurer = textMeasurer,
                graphOffset = extra,
                isSelected = state.isPickMode && task.id in state.pickedParentIds,
                isDragging = task.id == dragTaskId
            )
        }
    }
}

private fun taskDrawPosition(task: TaskUi, dragTaskId: Long, dragDelta: Offset): Offset {
    return if (task.id == dragTaskId) {
        Offset(task.posX + dragDelta.x, task.posY + dragDelta.y)
    } else {
        Offset(task.posX, task.posY)
    }
}

private fun findTaskAt(
    screenOffset: Offset,
    state: GraphState,
    dragTaskId: Long,
    dragGraphDelta: Offset
): TaskUi? {
    state.tasks.forEach { task ->
        val extra = if (task.id == dragTaskId) dragGraphDelta else Offset.Zero
        val adjusted = task.copy(
            posX = task.posX + extra.x,
            posY = task.posY + extra.y
        )
        if (taskNodeBounds(adjusted, state.scale, state.offset).contains(screenOffset)) {
            return task
        }
    }
    return null
}
