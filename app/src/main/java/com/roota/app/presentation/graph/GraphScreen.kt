package com.roota.app.presentation.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.roota.app.presentation.empty_pr.EmptyProjectScreen
import com.roota.app.presentation.graph.components.GraphCanvas
import com.roota.app.presentation.task_detail.TaskDetailSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(
    projectId: Long,
    navController: NavController,
    viewModel: GraphViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTaskId by remember { mutableStateOf<Long?>(null) }        // для BottomSheet
    var editTaskId by remember { mutableStateOf<Long?>(null) }           // для перехода в редактирование

    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Граф проекта") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("progress/$projectId")
                    }) {
                        Text("📊")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addNewTask(projectId) }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GraphCanvas(
                state = state,
                onDrag = viewModel::onDrag,
                onScale = viewModel::onScale,
                onTaskClick = { selectedTaskId = it },
                onTaskLongClick = { editTaskId = it }   // ← должен быть
            )

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.tasks.isEmpty()) {
                EmptyProjectScreen(
                    onAddFirstTaskClick = { viewModel.addNewTask(projectId) }
                )
            }
        }

        // BottomSheet — детали задачи
        selectedTaskId?.let { taskId ->
            TaskDetailSheet(
                taskId = taskId,
                onDismiss = { selectedTaskId = null }
            )
        }

        // Переход в редактирование по долгому нажатию
        editTaskId?.let { taskId ->
            LaunchedEffect(taskId) {
                navController.navigate("task_edit/$projectId?taskId=$taskId")
                editTaskId = null
            }
        }
    }
}