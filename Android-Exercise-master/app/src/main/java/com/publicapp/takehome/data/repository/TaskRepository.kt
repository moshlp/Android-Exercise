package com.publicapp.takehome.data.repository

import com.publicapp.takehome.data.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>
    suspend fun addTask(title: String, description: String): Result<Unit>
    suspend fun toggleCompleted(taskId: String): Result<Unit>
    suspend fun deleteTask(taskId: String): Result<Unit>
}
