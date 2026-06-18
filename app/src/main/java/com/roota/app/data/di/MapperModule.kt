package com.roota.app.data.di

import com.roota.app.data.mapper.DependencyMapper
import com.roota.app.data.mapper.ProjectMapper
import com.roota.app.data.mapper.TaskMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Provides
    @Singleton
    fun provideProjectMapper(): ProjectMapper = ProjectMapper

    @Provides
    @Singleton
    fun provideTaskMapper(): TaskMapper = TaskMapper

    @Provides
    @Singleton
    fun provideDependencyMapper(): DependencyMapper = DependencyMapper
}