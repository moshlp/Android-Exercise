package com.publicapp.takehome.di

import android.content.Context
import androidx.room.Room
import com.publicapp.takehome.data.local.AppDatabase
import com.publicapp.takehome.data.local.TaskDao
import com.publicapp.takehome.data.repository.RoomTaskRepository
import com.publicapp.takehome.data.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "todo_database"
        ).build()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao =
        db.taskDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        impl: RoomTaskRepository
    ): TaskRepository
}
