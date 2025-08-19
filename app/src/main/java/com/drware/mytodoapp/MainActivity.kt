/*
 * File: MainActivity.kt
 * Path: E:/Documents/Android_Studio_Projects/MyTodoApp/app/src/main/java/com/drware/mytodoapp/MainActivity.kt
 * Date: Mon Aug 18 18:02:23 2025
 * Purpose: This is the main screen (the "View"). It now correctly provides the
 * repository to the ViewModelFactory, completing the MVVM refactor.
 */

package com.drware.mytodoapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter
    private lateinit var rvTodoItems: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton

    // ✅ Changed: We now get the repository from our Application class and pass it to the factory.
    private val todoViewModel: TodoViewModel by viewModels {
        TodoViewModelFactory((application as TodoApplication).repository)
    }

    private var activeTodoEditText: EditText? = null
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) { launchSpeechToText() } else {
            Toast.makeText(this, "Microphone permission is required", Toast.LENGTH_SHORT).show()
        }
    }
    private val speechToTextLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val spokenText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            activeTodoEditText?.setText(spokenText)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoAdapter = TodoAdapter(
            mutableListOf(),
            { position ->
                val todoToEdit = todoAdapter.getTodoAt(position)
                showAddOrEditTodoDialog(todoToEdit)
            },
            { position, isChecked ->
                updateTodoCheckedState(position, isChecked)
            }
        )

        rvTodoItems = findViewById(R.id.rvTodoItems)
        fabAddTask = findViewById(R.id.fabAddTask)
        rvTodoItems.adapter = todoAdapter
        rvTodoItems.layoutManager = LinearLayoutManager(this)

        fabAddTask.setOnClickListener {
            showAddOrEditTodoDialog(null)
        }

        setupSwipeToDelete()

        lifecycleScope.launch {
            todoViewModel.allTodos.collect { todos ->
                todoAdapter.updateTodos(todos.toMutableList())
            }
        }
    }

    private fun showAddOrEditTodoDialog(todo: Todo?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_todo, null)
        val etTodoTitle = dialogView.findViewById<EditText>(R.id.etTodoTitle)
        val btnVoiceInput = dialogView.findViewById<ImageButton>(R.id.btnVoiceInput)

        activeTodoEditText = etTodoTitle
        val dialogTitle = if (todo == null) "Add New Todo" else "Edit Todo"
        val positiveButtonTitle = if (todo == null) "Add" else "Save"
        etTodoTitle.setText(todo?.title ?: "")

        btnVoiceInput.setOnClickListener {
            checkPermissionAndLaunchSpeechToText()
        }

        AlertDialog.Builder(this)
            .setTitle(dialogTitle)
            .setView(dialogView)
            .setPositiveButton(positiveButtonTitle) { _, _ ->
                val todoTitle = etTodoTitle.text.toString()
                if (todoTitle.isNotBlank()) {
                    if (todo == null) {
                        todoViewModel.insert(Todo(title = todoTitle))
                    } else {
                        todoViewModel.update(todo.copy(title = todoTitle))
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .setOnDismissListener { activeTodoEditText = null }
            .show()
    }

    private fun checkPermissionAndLaunchSpeechToText() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED -> {
                launchSpeechToText()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun launchSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the task title")
        }
        try {
            speechToTextLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition is not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTodoCheckedState(position: Int, isChecked: Boolean) {
        val itemToUpdate = todoAdapter.getTodoAt(position)
        todoViewModel.update(itemToUpdate.copy(isChecked = isChecked))
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val todoToDelete = todoAdapter.getTodoAt(position)
                    todoViewModel.delete(todoToDelete)

                    Snackbar.make(findViewById(R.id.coordinatorLayout), "Task deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            todoViewModel.undoDelete()
                        }
                        .show()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvTodoItems)
    }
}

/* EOF */