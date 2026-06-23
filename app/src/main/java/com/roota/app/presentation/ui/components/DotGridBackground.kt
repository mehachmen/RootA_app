package com.roota.app.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.roota.app.presentation.ui.theme.DarkBackground
import com.roota.app.presentation.ui.theme.GridColor

@Composable
fun DotGridBackground(
    modifier: Modifier = Modifier,
    spacing: Float = 24f,
    dotRadius: Float = 1.5f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(DarkBackground)
        val cols = (size.width / spacing).toInt() + 2
        val rows = (size.height / spacing).toInt() + 2
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val x = col * spacing
                val y = row * spacing
                drawCircle(
                    color = GridColor,
                    radius = dotRadius,
                    center = Offset(x, y)
                )
            }
        }
    }
}
