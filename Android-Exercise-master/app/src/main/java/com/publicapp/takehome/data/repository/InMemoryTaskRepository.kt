package com.publicapp.takehome.data.repository

import com.publicapp.takehome.data.model.Task
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import kotlin.random.Random

class InMemoryTaskRepository : TaskRepository {

    private val tasks = MutableStateFlow<List<Task>>(emptyList())

    override fun observeTasks(): Flow<List<Task>> = tasks

    override suspend fun addTask(title: String, description: String): Result<Unit> {
        val delayMs = Random.nextLong(500L, 2501L)
        delay(delayMs)

        val success = Random.nextFloat() < 0.75f
        if (!success) return Result.failure(RuntimeException("Network error"))

        val newTask = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description
        )

        tasks.update { listOf(newTask) + it }
        return Result.success(Unit)
    }

    override suspend fun toggleCompleted(taskId: String): Result<Unit> {
        tasks.update { list ->
            list.map { if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it }
        }
        return Result.success(Unit)
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        tasks.update { list -> list.filterNot { it.id == taskId } }
        return Result.success(Unit)
    }

    override suspend fun restoreTask(task: Task): Result<Unit> {
        tasks.update { list ->
            if (list.any { it.id == task.id }) list else listOf(task) + list
        }
        return Result.success(Unit)
    }
}
