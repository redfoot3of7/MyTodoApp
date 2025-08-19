/*
 * File: TodoAdapter.kt
 * Path: E:/Documents/Android_Studio_Projects/MyTodoApp/app/src/main/java/com/drware/mytodoapp/TodoAdapter.kt
 * Date: Mon Aug 18 17:06:27 2025
 * Purpose: This adapter manages the list of to-do items for the RecyclerView. It handles
 * item clicks for editing and checkbox changes to support a fully interactive list.
 */

package com.drware.mytodoapp

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drware.mytodoapp.databinding.ItemTodoBinding

// ✅ This is the correct constructor that MainActivity is trying to call.
class TodoAdapter(
    private var todos: MutableList<Todo>,
    private val onItemClick: (position: Int) -> Unit,
    private val onCheckedChange: (position: Int, isChecked: Boolean) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentTodo = todos[position]
        holder.binding.apply {
            tvTodoTitle.text = currentTodo.title
            cbDone.setOnCheckedChangeListener(null)
            cbDone.isChecked = currentTodo.isChecked
            toggleStrikeThrough(tvTodoTitle, currentTodo.isChecked)

            // ✅ This listener handles the click for editing the task.
            tvTodoTitle.setOnClickListener {
                val currentPosition = holder.bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onItemClick(currentPosition)
                }
            }

            // This listener handles the checkbox state change.
            cbDone.setOnCheckedChangeListener { _, isChecked ->
                val currentPosition = holder.bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    toggleStrikeThrough(tvTodoTitle, isChecked)
                    onCheckedChange(currentPosition, isChecked)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    fun getTodoAt(position: Int): Todo {
        return todos[position]
    }

    fun updateTodos(newTodos: MutableList<Todo>) {
        todos.clear()
        todos.addAll(newTodos)
        notifyDataSetChanged()
    }

    private fun toggleStrikeThrough(tvTodoTitle: android.widget.TextView, isChecked: Boolean) {
        if (isChecked) {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}

/* EOF */