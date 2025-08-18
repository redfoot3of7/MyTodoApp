package com.drware.mytodoapp

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drware.mytodoapp.databinding.ItemTodoBinding

class TodoAdapter(
    private var todos: MutableList<Todo>,
    private val onCheckedChange: (position: Int, isChecked: Boolean) -> Unit,
    private val onDeleteClick: (position: Int) -> Unit
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

            cbDone.setOnCheckedChangeListener { _, isChecked ->
                val currentPosition = holder.bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    toggleStrikeThrough(tvTodoTitle, isChecked)
                    onCheckedChange(currentPosition, isChecked)
                }
            }

            // ✅ This block now exactly matches the robust code example
            ivDelete.setOnClickListener {
                // Use the safer bindingAdapterPosition property
                val position = holder.bindingAdapterPosition

                // Always check for a valid position before calling your action
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(position)
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