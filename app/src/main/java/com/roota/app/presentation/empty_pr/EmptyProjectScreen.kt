package com.roota.app.presentation.empty_pr

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmptyProjectScreen(
    onAddFirstTaskClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Иконка пустого состояния (можно заменить на Image или Icon)
            Text(
                text = "🌿",
                fontSize = 80.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Здесь пока пусто",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Создайте первую задачу, чтобы начать строить граф проекта",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onAddFirstTaskClick,
                modifier = Modifier
                    .width(280.dp)
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    "Добавить первую задачу",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Или нажмите + в правом нижнем углу",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}