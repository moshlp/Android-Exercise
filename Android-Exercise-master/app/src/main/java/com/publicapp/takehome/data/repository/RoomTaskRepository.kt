package com.publicapp.takehome.data.repository

import com.publicapp.takehome.data.local.TaskDao
import com.publicapp.takehome.data.model.Task
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

class RoomTaskRepository @Inject constructor(
    private val dao: TaskDao
) : TaskRepository {

    override fun observeTasks(): Flow<List<Task>> =
        dao.observeTasks()

    override suspend fun addTask(title: String, description: String): Result<Unit> {
        delay(Random.nextLong(500, 2500))

        val success = Random.nextInt(100) < 75
        if (!success) return Result.failure(Exception("Network error"))

        val task = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            isCompleted = false,
            createdAt = System.currentTimeMillis()
        )

        dao.insert(task)
        return Result.success(Unit)
    }

    override suspend fun toggleCompleted(taskId: String): Result<Unit> {
        val task = dao.getById(taskId) ?: return Result.failure(NoSuchElementException("Task not found"))
        dao.update(task.copy(isCompleted = !task.isCompleted))
        return Result.success(Unit)
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        dao.delete(taskId)
        return Result.success(Unit)
    }

    override suspend fun restoreTask(task: Task): Result<Unit> {
        dao.insert(task)
        return Result.success(Unit)
    }
}
