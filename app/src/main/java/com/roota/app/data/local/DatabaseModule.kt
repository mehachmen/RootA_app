package com.roota.app.data.local

import android.content.Context
import androidx.room.Room
import com.roota.app.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "roota_database.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideProjectDao(database: AppDatabase) = database.projectDao()

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase) = database.taskDao()

    @Provides
    @Singleton
    fun provideDependencyDao(database: AppDatabase) = database.dependencyDao()
}