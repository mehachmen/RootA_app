package com.roota.app.presentation.project_create

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.roota.app.presentation.ui.theme.RootATheme

@OptIn(ExperimentalMaterial3Api::class) // чтобы не ругался
@Composable
fun CreateProjectScreen(
    onProjectCreated: () -> Unit,
    viewModel: CreateProjectViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    RootATheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Новый проект") })
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { viewModel.onTitleChange(it) },
                    label = { Text("Название проекта") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.description,
                    onValueChange = { viewModel.onDescriptionChange(it) },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.createProject()
                        onProjectCreated()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.title.isNotBlank()
                ) {
                    Text("Создать проект")
                }
            }
        }
    }
}