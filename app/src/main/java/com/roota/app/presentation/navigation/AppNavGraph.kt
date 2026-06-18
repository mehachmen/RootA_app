package com.roota.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.roota.app.presentation.empty_pr.EmptyProjectScreen
import com.roota.app.presentation.onboarding.OnboardingScreen
import com.roota.app.presentation.graph.GraphScreen
import com.roota.app.presentation.progress.ProgressScreen
import com.roota.app.presentation.project.ProjectListScreen
import com.roota.app.presentation.project_create.CreateProjectScreen
import com.roota.app.presentation.settings.SettingsScreen
import com.roota.app.presentation.task_edit.TaskEditScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "projects"
    ) {

        // SCR-01
        composable("onboarding") {
            OnboardingScreen(
                onStartClick = {
                    navController.navigate("projects") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // SCR-03 — Список проектов
        composable("projects") {
            ProjectListScreen(
                onCreateProjectClick = { navController.navigate("create_project") },
                onProjectClick = { projectId ->
                    navController.navigate("graph/$projectId")
                }
            )
        }

        // SCR-02 — Создание проекта
        composable("create_project") {
            CreateProjectScreen(
                onProjectCreated = { navController.popBackStack() }
            )
        }

        // SCR-04 — Граф проекта (главный экран)
        composable(
            route = "graph/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
            GraphScreen(
                projectId = projectId,
                navController = navController
            )
        }

        // SCR-06 — Создание / Редактирование задачи
        composable(
            route = "task_edit/{projectId}?taskId={taskId}",
            arguments = listOf(
                navArgument("projectId") {
                    type = NavType.LongType
                },
                navArgument("taskId") {
                    type = NavType.LongType
                    // ❌ НЕ используем nullable
                    defaultValue = 0L  // ← 0 означает "новая задача"
                }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L  // ← если null, то 0

            TaskEditScreen(
                projectId = projectId,
                taskId = if (taskId == 0L) null else taskId,  // ← преобразуем 0 в null
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        // SCR-07 — Прогресс проекта
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

        // SCR-08 — Настройки
        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}