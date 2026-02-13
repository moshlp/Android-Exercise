package com.publicapp.takehome.di

import com.publicapp.takehome.data.repository.InMemoryTaskRepository
import com.publicapp.takehome.data.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTaskRepository(): TaskRepository = InMemoryTaskRepository()
}
