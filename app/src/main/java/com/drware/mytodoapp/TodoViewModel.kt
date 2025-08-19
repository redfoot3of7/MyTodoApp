/*
 * File: TodoViewModel.kt
 * Path: E:/Documents/Android_Studio_Projects/MyTodoApp/app/src/main/java/com/drware/mytodoapp/TodoViewModel.kt
 * Date: Mon Aug 18 18:02:23 2025
 * Purpose: This ViewModel now gets its data from the TodoRepository, completely decoupling
 * it from the Room database implementation (the DAO).
 */

package com.drware.mytodoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// ✅ Changed: The constructor now takes a TodoRepository.
class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    // ✅ Changed: The Flow of todos is now fetched from the repository.
    val allTodos: Flow<List<Todo>> = repository.allTodos

    private var recentlyDeletedTodo: Todo? = null

    // ✅ Changed: All data operations are now sent to the repository.
    fun insert(todo: Todo) = viewModelScope.launch {
        repository.insert(todo)
    }

    fun update(todo: Todo) = viewModelScope.launch {
        repository.update(todo)
    }

    fun delete(todo: Todo) = viewModelScope.launch {
        recentlyDeletedTodo = todo
        repository.delete(todo)
    }

    fun undoDelete() {
        recentlyDeletedTodo?.let { todo ->
            insert(todo)
        }
    }
}

// ✅ Changed: The Factory now uses the TodoRepository to create the ViewModel.
class TodoViewModelFactory(private val repository: TodoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/* EOF */