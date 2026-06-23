package com.roota.app.presentation.graph.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.DarkSurface

@Composable
fun ZoomControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        SmallFloatingActionButton(
            onClick = onZoomIn,
            containerColor = DarkSurface,
            contentColor = AccentGreen,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("+", style = MaterialTheme.typography.titleMedium)
        }
        SmallFloatingActionButton(
            onClick = onZoomOut,
            containerColor = DarkSurface,
            contentColor = AccentGreen,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("−", style = MaterialTheme.typography.titleMedium)
        }
    }
}
