/*
 * File: TodoApplication.kt
 * Path: E:/Documents/Android_Studio_Projects/MyTodoApp/app/src/main/java/com/drware/mytodoapp/TodoApplication.kt
 * Date: Mon Aug 18 17:59:10 2025
 * Purpose: This custom Application class now provides single, app-wide instances of both
 * the database and the repository.
 */

package com.drware.mytodoapp

import android.app.Application

class TodoApplication : Application() {
    // The database instance is created lazily.
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    // ✅ New: The repository instance is also created lazily, using the database's DAO.
    val repository: TodoRepository by lazy { TodoRepository(database.todoDao()) }
}

/* EOF */