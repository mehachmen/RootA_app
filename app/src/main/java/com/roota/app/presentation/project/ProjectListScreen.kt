package com.roota.app.presentation.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.roota.app.R
import com.roota.app.presentation.ui.components.RootAIcons
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.CardShape
import com.roota.app.presentation.ui.theme.DarkBackground
import com.roota.app.presentation.ui.theme.DarkSurface
import com.roota.app.presentation.ui.theme.Dimens
import com.roota.app.presentation.ui.theme.TextPrimary
import com.roota.app.presentation.ui.theme.TextSecondary

@Composable
fun ProjectListScreen(
    onCreateProjectClick: () -> Unit,
    onProjectClick: (Long) -> Unit,
    viewModel: ProjectListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var projectToDelete by remember { mutableStateOf<ProjectCardUi?>(null) }

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateProjectClick,
                containerColor = AccentGreen,
                contentColor = DarkBackground,
                icon = {
                    Icon(
                        imageVector = RootAIcons.Add,
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.projects_fab),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ProjectListHeader(projectCount = uiState.projectCount)

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentGreen)
                    }
                }
                uiState.projects.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.projects_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = Dimens.screenPadding,
                            end = Dimens.screenPadding,
                            bottom = 88.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(Dimens.itemSpacing)
                    ) {
                        items(uiState.projects, key = { it.id }) { project ->
                            ProjectCard(
                                project = project,
                                onClick = { onProjectClick(project.id) },
                                onLongClick = { projectToDelete = project }
                            )
                        }
                    }
                }
            }
        }
    }

    val pendingDelete = projectToDelete
    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = { Text(stringResource(R.string.delete_project_title)) },
            text = {
                Text(stringResource(R.string.delete_project_message, pendingDelete.title))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProject(pendingDelete.id)
                        projectToDelete = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
private fun ProjectListHeader(projectCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.screenPadding, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = AccentGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = pluralStringResource(R.plurals.projects_count, projectCount, projectCount),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.projects_title),
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjectCard(
    project: ProjectCardUi,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val accent = Color(project.colorTagArgb)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(accent)
            )
            Column(modifier = Modifier.padding(Dimens.cardPadding)) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                project.description?.takeIf { it.isNotBlank() }?.let { desc ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.projects_tasks_count, project.taskCount),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "${project.progressPercent.toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { (project.progressPercent / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.miniProgressHeight),
                    color = accent,
                    trackColor = TextSecondary.copy(alpha = 0.2f)
                )
            }
        }
    }
}
