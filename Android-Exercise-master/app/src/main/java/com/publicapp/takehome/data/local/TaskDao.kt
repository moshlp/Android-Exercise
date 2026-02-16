package com.publicapp.takehome.data.local

import androidx.room.*
import com.publicapp.takehome.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    fun observeTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun delete(taskId: String)

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getById(taskId: String): Task?
}
