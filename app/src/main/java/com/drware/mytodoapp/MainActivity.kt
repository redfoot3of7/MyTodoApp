/*
 * File: MainActivity.kt
 * Path: E:/Documents/Android_Studio_Projects/MyTodoApp/app/src/main/java/com/drware/mytodoapp/MainActivity.kt
 * Date: Mon Aug 18 12:24:11 2025
 * Purpose: This is the main screen of the application. It displays the list of to-do items
 * and handles user interactions like adding, deleting, and updating tasks.
 */

package com.drware.mytodoapp

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter
    private lateinit var rvTodoItems: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var db: AppDatabase
    private lateinit var todoDao: TodoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the database and DAO
        db = AppDatabase.getDatabase(this)
        todoDao = db.todoDao()

        // Initialize the adapter with an empty list and provide both lambdas
        todoAdapter = TodoAdapter(
            mutableListOf(),
            // 1. Lambda for checkbox changes
            { position, isChecked ->
                updateTodoCheckedState(position, isChecked)
            },
            // 2. Lambda for delete clicks
            { position ->
                deleteTodo(position)
            }
        )

        // Initialize UI components
        rvTodoItems = findViewById(R.id.rvTodoItems)
        fabAddTask = findViewById(R.id.fabAddTask)
        rvTodoItems.adapter = todoAdapter
        rvTodoItems.layoutManager = LinearLayoutManager(this)

        // Set listener for the Floating Action Button
        fabAddTask.setOnClickListener {
            showAddTodoDialog()
        }

        // Collect all items from the database and update the list reactively
        lifecycleScope.launch {
            todoDao.getAll().collect { todos ->
                todoAdapter.updateTodos(todos.toMutableList())
            }
        }
    }

    private fun showAddTodoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_todo, null)
        val etTodoTitle = dialogView.findViewById<EditText>(R.id.etTodoTitle)

        AlertDialog.Builder(this)
            .setTitle("Add New Todo")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val todoTitle = etTodoTitle.text.toString()
                if (todoTitle.isNotBlank()) {
                    lifecycleScope.launch {
                        val newTodo = Todo(title = todoTitle)
                        todoDao.insert(newTodo)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteTodo(position: Int) {
        val itemToDelete = todoAdapter.getTodoAt(position)
        lifecycleScope.launch {
            todoDao.delete(itemToDelete)
        }
    }

    private fun updateTodoCheckedState(position: Int, isChecked: Boolean) {
        val itemToUpdate = todoAdapter.getTodoAt(position)
        val updatedItem = itemToUpdate.copy(isChecked = isChecked)

        lifecycleScope.launch {
            todoDao.update(updatedItem)
        }
    }
}

/* EOF */