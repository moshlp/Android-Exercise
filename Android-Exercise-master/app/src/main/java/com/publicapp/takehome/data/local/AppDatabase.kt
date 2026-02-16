package com.publicapp.takehome.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.publicapp.takehome.data.model.Task

@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
