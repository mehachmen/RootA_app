package com.roota.app.presentation.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.roota.app.R
import com.roota.app.presentation.ui.components.GlowProgressRing
import com.roota.app.presentation.ui.components.RootATopBar
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.CardShape
import com.roota.app.presentation.ui.theme.DarkBackground
import com.roota.app.presentation.ui.theme.DarkSurface
import com.roota.app.presentation.ui.theme.Dimens
import com.roota.app.presentation.ui.theme.InProgressYellow
import com.roota.app.presentation.ui.theme.TextPrimary
import com.roota.app.presentation.ui.theme.TextSecondary

@Composable
fun GlobalProgressScreen(
    viewModel: GlobalProgressViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            RootATopBar(title = stringResource(R.string.nav_progress))
        }
    ) { padding ->
        ProgressContent(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(padding)
        )
    }
}

@Composable
fun ProgressScreen(
    projectId: Long,
    onBackClick: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffectProject(projectId, viewModel)

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            RootATopBar(
                title = stringResource(R.string.progress_title),
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        ProgressContent(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(padding)
        )
    }
}

@Composable
private fun LaunchedEffectProject(projectId: Long, viewModel: ProgressViewModel) {
    androidx.compose.runtime.LaunchedEffect(projectId) {
        viewModel.loadProjectProgress(projectId)
    }
}

@Composable
private fun ProgressContent(state: ProgressState, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (state.isLoading) {
            CircularProgressIndicator(color = AccentGreen)
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.sectionSpacing),
                modifier = Modifier.padding(Dimens.screenPadding)
            ) {
                GlowProgressRing(
                    progress = (state.progressPercent / 100f).coerceIn(0f, 1f),
                    label = "${state.progressPercent.toInt()}%",
                    sublabel = stringResource(R.string.progress_done_label)
                )

                Card(
                    modifier = Modifier.width(320.dp),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(Dimens.cardPadding)) {
                        Text(
                            text = stringResource(R.string.progress_stats_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StatRow(
                            label = stringResource(R.string.progress_total),
                            value = state.totalTasks.toString()
                        )
                        StatRow(
                            label = stringResource(R.string.progress_completed),
                            value = state.completedTasks.toString(),
                            color = AccentGreen
                        )
                        StatRow(
                            label = stringResource(R.string.progress_in_progress),
                            value = state.inProgressTasks.toString(),
                            color = InProgressYellow
                        )
                        StatRow(
                            label = stringResource(R.string.progress_not_started),
                            value = state.notStartedTasks.toString()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, color: Color = TextPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}
