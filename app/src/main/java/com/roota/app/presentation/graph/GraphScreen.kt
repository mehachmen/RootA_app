package com.roota.app.presentation.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.roota.app.R
import com.roota.app.presentation.empty_pr.EmptyProjectScreen
import com.roota.app.presentation.graph.components.GraphCanvas
import com.roota.app.presentation.graph.components.GraphPickModeBar
import com.roota.app.presentation.graph.components.MiniMap
import com.roota.app.presentation.graph.components.ZoomControls
import com.roota.app.presentation.task_detail.TaskDetailSheet
import com.roota.app.presentation.ui.components.RootAIconButton
import com.roota.app.presentation.ui.components.RootAIcons
import com.roota.app.presentation.ui.components.RootATopBar
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.DarkBackground

@Composable
fun GraphScreen(
    projectId: Long,
    navController: NavController,
    pickForTaskId: Long = -1L,
    initialPickParents: List<Long> = emptyList(),
    viewModel: GraphViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTaskId by remember { mutableStateOf<Long?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    val isPickMode = pickForTaskId >= 0L

    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }

    DisposableEffect(projectId) {
        onDispose { viewModel.saveViewport() }
    }

    LaunchedEffect(pickForTaskId, initialPickParents) {
        if (isPickMode) {
            viewModel.enterPickMode(pickForTaskId, initialPickParents)
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    val topBarTitle = when {
        isPickMode -> stringResource(R.string.graph_pick_deps)
        state.projectTitle.isNotBlank() -> state.projectTitle
        else -> stringResource(R.string.graph_title)
    }

    Scaffold(
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RootATopBar(
                title = topBarTitle,
                onBackClick = { navController.popBackStack() },
                actions = {
                    if (!isPickMode && state.tasks.isNotEmpty()) {
                        TextButton(onClick = { viewModel.autoLayout() }) {
                            Text(
                                text = stringResource(R.string.graph_align),
                                color = AccentGreen
                            )
                        }
                    }
                    if (!isPickMode) {
                        RootAIconButton(
                            icon = RootAIcons.Progress,
                            contentDescription = stringResource(R.string.graph_progress),
                            onClick = { navController.navigate("progress/$projectId") },
                            tint = AccentGreen
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isPickMode) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate("task_edit/$projectId") },
                    containerColor = AccentGreen,
                    contentColor = DarkBackground,
                    icon = {
                        Icon(imageVector = RootAIcons.Add, contentDescription = null)
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.graph_add_task),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                )
            }
        },
        bottomBar = {
            if (isPickMode) {
                GraphPickModeBar(
                    selectedCount = state.pickedParentIds.size,
                    onDone = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("picked_parents", viewModel.getPickedParentIds())
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .onSizeChanged { size ->
                    canvasSize = Size(size.width.toFloat(), size.height.toFloat())
                }
        ) {
            GraphCanvas(
                state = state.copy(isPickMode = isPickMode, pickForTaskId = pickForTaskId),
                onCanvasDrag = viewModel::onCanvasDrag,
                onScale = viewModel::onScale,
                onTaskClick = { if (!isPickMode) selectedTaskId = it },
                onTaskPositionCommitted = viewModel::onTaskPositionCommitted,
                onTogglePickParent = viewModel::togglePickParent
            )

            if (!isPickMode && state.tasks.isNotEmpty()) {
                ZoomControls(
                    onZoomIn = viewModel::zoomIn,
                    onZoomOut = viewModel::zoomOut,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )

                MiniMap(
                    state = state,
                    canvasSize = canvasSize,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AccentGreen
                )
            } else if (state.tasks.isEmpty() && !isPickMode) {
                EmptyProjectScreen(
                    onAddFirstTaskClick = {
                        navController.navigate("task_edit/$projectId")
                    }
                )
            }
        }

        if (!isPickMode) {
            selectedTaskId?.let { taskId ->
                TaskDetailSheet(
                    taskId = taskId,
                    onDismiss = { selectedTaskId = null },
                    onEditClick = {
                        selectedTaskId = null
                        navController.navigate("task_edit/$projectId?taskId=$taskId")
                    }
                )
            }
        }
    }
}
