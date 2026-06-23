package com.roota.app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.roota.app.data.local.AppDatabase
import com.roota.app.data.local.entity.UserSettingsEntity
import com.roota.app.data.mapper.DependencyMapper
import com.roota.app.data.mapper.ProjectMapper
import com.roota.app.data.mapper.TaskMapper
import com.roota.app.data.repo.DependencyRepositoryImpl
import com.roota.app.data.repo.ProjectRepositoryImpl
import com.roota.app.data.repo.TaskRepositoryImpl
import com.roota.app.domain.model.Dependency
import com.roota.app.domain.model.TaskPriority
import com.roota.app.domain.usecase.project.CreateProjectUseCase
import com.roota.app.domain.usecase.settings.ClearAllDataUseCase
import com.roota.app.domain.usecase.task.CreateTaskUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataLayerIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var projectRepository: ProjectRepositoryImpl
    private lateinit var taskRepository: TaskRepositoryImpl
    private lateinit var dependencyRepository: DependencyRepositoryImpl
    private lateinit var createProjectUseCase: CreateProjectUseCase
    private lateinit var createTaskUseCase: CreateTaskUseCase
    private lateinit var clearAllDataUseCase: ClearAllDataUseCase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        projectRepository = ProjectRepositoryImpl(database.projectDao(), ProjectMapper)
        taskRepository = TaskRepositoryImpl(database.taskDao(), TaskMapper)
        dependencyRepository = DependencyRepositoryImpl(database.dependencyDao(), DependencyMapper)
        createProjectUseCase = CreateProjectUseCase(projectRepository)
        createTaskUseCase = CreateTaskUseCase(taskRepository)
        clearAllDataUseCase = ClearAllDataUseCase(database, database.userSettingsDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun createProjectAndTask_taskAppearsInProjectList() = runTest {
        val projectId = createProjectUseCase(title = "Интеграционный проект")
        createTaskUseCase(
            projectId = projectId,
            title = "Задача A",
            priority = TaskPriority.HIGH
        )

        val tasks = taskRepository.getTasksByProject(projectId).first()

        assertEquals(1, tasks.size)
        assertEquals("Задача A", tasks[0].title)
        assertEquals(TaskPriority.HIGH, tasks[0].priority)
    }

    @Test
    fun deleteProject_cascadesTasksAndDependencies() = runTest {
        val projectId = createProjectUseCase(title = "Проект для удаления")
        val taskAId = createTaskUseCase(projectId = projectId, title = "A")
        val taskBId = createTaskUseCase(projectId = projectId, title = "B")

        dependencyRepository.addDependency(
            Dependency(sourceTaskId = taskAId, targetTaskId = taskBId)
        )

        val project = projectRepository.getProjectById(projectId)!!
        projectRepository.deleteProject(project)

        assertNull(projectRepository.getProjectById(projectId))
        assertTrue(taskRepository.getTasksByProject(projectId).first().isEmpty())
        assertTrue(dependencyRepository.getAllDependencies().isEmpty())
    }

    @Test
    fun addDependency_persistsAndCanBeQueriedByProject() = runTest {
        val projectId = createProjectUseCase(title = "Граф зависимостей")
        val parentId = createTaskUseCase(projectId = projectId, title = "Родитель")
        val childId = createTaskUseCase(projectId = projectId, title = "Потомок")

        dependencyRepository.addDependency(
            Dependency(sourceTaskId = parentId, targetTaskId = childId)
        )

        val deps = dependencyRepository.getAllDependenciesForProject(projectId).first()

        assertEquals(1, deps.size)
        assertEquals(parentId, deps[0].sourceTaskId)
        assertEquals(childId, deps[0].targetTaskId)
    }

    @Test
    fun clearAllData_removesProjectsAndResetsOnboarding() = runTest {
        createProjectUseCase(title = "Временный проект")
        database.userSettingsDao().insertSettings(
            UserSettingsEntity(onboardingCompleted = true)
        )

        clearAllDataUseCase()

        assertTrue(projectRepository.getAllProjects().first().isEmpty())
        val settings = database.userSettingsDao().getSettings().first()
        assertNotNull(settings)
        assertEquals(false, settings?.onboardingCompleted)
    }
}
