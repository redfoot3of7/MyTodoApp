/*
 * File: TodoRepository.kt
 * Path: E:/Documents/Android_Studio_Projects/MyTodoApp/app/src/main/java/com/drware/mytodoapp/TodoRepository.kt
 * Date: Mon Aug 18 17:59:10 2025
 * Purpose: This class acts as a single source of truth for all app data. It abstracts the
 * data source (Room DAO) from the rest of the application, like the ViewModel.
 */

package com.drware.mytodoapp

import kotlinx.coroutines.flow.Flow

// The repository's constructor takes the DAO as its dependency.
class TodoRepository(private val todoDao: TodoDao) {

    // The Flow of todos is fetched from the DAO.
    val allTodos: Flow<List<Todo>> = todoDao.getAll()

    // These suspend functions provide a clean API for the ViewModel to call
    // for data modification, without exposing the DAO directly.
    suspend fun insert(todo: Todo) {
        todoDao.insert(todo)
    }

    suspend fun update(todo: Todo) {
        todoDao.update(todo)
    }

    suspend fun delete(todo: Todo) {
        todoDao.delete(todo)
    }
}

/* EOF */