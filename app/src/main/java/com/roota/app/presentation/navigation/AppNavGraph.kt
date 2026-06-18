package com.roota.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.roota.app.presentation.graph.GraphScreen
import com.roota.app.presentation.onboarding.OnboardingScreen
import com.roota.app.presentation.onboarding.OnboardingViewModel
import com.roota.app.presentation.progress.GlobalProgressScreen
import com.roota.app.presentation.progress.ProgressScreen
import com.roota.app.presentation.project.ProjectListScreen
import com.roota.app.presentation.project_create.CreateProjectScreen
import com.roota.app.presentation.settings.SettingsScreen
import com.roota.app.presentation.task_edit.TaskEditScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("onboarding") {
            val onboardingViewModel: OnboardingViewModel = hiltViewModel()
            OnboardingScreen(
                onStartClick = {
                    onboardingViewModel.completeOnboarding()
                    navController.navigate("projects") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("projects") {
            ProjectListScreen(
                onCreateProjectClick = { navController.navigate("create_project") },
                onProjectClick = { projectId ->
                    navController.navigate("graph/$projectId")
                }
            )
        }

        composable("global_progress") {
            GlobalProgressScreen()
        }

        composable("settings") {
            SettingsScreen(showBackButton = false)
        }

        composable("create_project") {
            CreateProjectScreen(
                onProjectCreated = { projectId ->
                    navController.navigate("graph/$projectId") {
                        popUpTo("create_project") { inclusive = true }
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = "graph/{projectId}?pickForTask={pickForTask}",
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType },
                navArgument("pickForTask") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
            val pickForTask = backStackEntry.arguments?.getLong("pickForTask") ?: -1L
            val initialParents = navController.previousBackStackEntry
                ?.savedStateHandle?.get<List<Long>>("pick_initial_parents") ?: emptyList()

            if (initialParents.isNotEmpty()) {
                navController.previousBackStackEntry?.savedStateHandle
                    ?.remove<List<Long>>("pick_initial_parents")
            }

            GraphScreen(
                projectId = projectId,
                navController = navController,
                pickForTaskId = pickForTask,
                initialPickParents = initialParents
            )
        }

        composable(
            route = "task_edit/{projectId}?taskId={taskId}",
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType },
                navArgument("taskId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L

            TaskEditScreen(
                projectId = projectId,
                taskId = if (taskId == 0L) null else taskId,
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                navController = navController
            )
        }

        composable(
            route = "progress/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
            ProgressScreen(
                projectId = projectId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
