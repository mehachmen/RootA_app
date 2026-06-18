package com.roota.app.presentation.empty_pr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.roota.app.R
import com.roota.app.presentation.ui.components.DotGridBackground
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.ButtonShape
import com.roota.app.presentation.ui.theme.Dimens
import com.roota.app.presentation.ui.theme.TextPrimary
import com.roota.app.presentation.ui.theme.TextSecondary

@Composable
fun EmptyProjectScreen(
    onAddFirstTaskClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        DotGridBackground(modifier = Modifier.fillMaxSize())

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.empty_graph_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.empty_graph_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onAddFirstTaskClick,
                modifier = Modifier
                    .width(280.dp)
                    .height(Dimens.buttonHeight),
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGreen,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = stringResource(R.string.empty_graph_cta),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.empty_graph_hint),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
