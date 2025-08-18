/*
 * File: TodoDao.kt
 * Path: E:/Documents/Android_Studio_Projects/MyTodoApp/app/src/main/java/com/drware/mytodoapp/TodoDao.kt
 * Date: Mon Aug 18 12:24:11 2025
 * Purpose: This file defines the Data Access Object (DAO) for the Todo entity.
 * It specifies the SQL queries and associates them with method calls for database operations.
 */

package com.drware.mytodoapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    fun getAll(): Flow<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)
}

/* EOF */