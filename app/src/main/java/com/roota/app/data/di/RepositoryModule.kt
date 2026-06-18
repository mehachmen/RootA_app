package com.roota.app.data.di

import com.roota.app.domain.repository.DependencyRepository
import com.roota.app.domain.repository.TaskRepository
import com.roota.app.domain.repository.ProjectRepository
import com.roota.app.domain.repository.UserSettingsRepository
import com.roota.app.data.repo.DependencyRepositoryImpl
import com.roota.app.data.repo.ProjectRepositoryImpl
import com.roota.app.data.repo.TaskRepositoryImpl
import com.roota.app.data.repo.UserSettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProjectRepository(impl: ProjectRepositoryImpl): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindDependencyRepository(impl: DependencyRepositoryImpl): DependencyRepository

    @Binds
    @Singleton
    abstract fun bindUserSettingsRepository(impl: UserSettingsRepositoryImpl): UserSettingsRepository
}