package com.roota.app.presentation.progress

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    projectId: Long,
    onBackClick: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(projectId) {
        viewModel.loadProjectProgress(projectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Прогресс проекта") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Круговой прогресс
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { (state.progressPercent / 100f).coerceIn(0f, 1f) },
                            strokeWidth = 16.dp,
                            modifier = Modifier.size(200.dp),
                            color = Color(0xFF00E676)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${state.progressPercent.toInt()}%",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Выполнено", style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    // Статистика
                    Card(modifier = Modifier.width(320.dp)) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            StatRow("Всего задач", state.totalTasks.toString())
                            StatRow("Выполнено", state.completedTasks.toString(), Color(0xFF00E676))
                            StatRow("В процессе", state.inProgressTasks.toString(), Color(0xFFFFC107))
                            StatRow("Не начато", state.notStartedTasks.toString())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}