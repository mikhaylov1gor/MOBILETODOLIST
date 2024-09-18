package com.hitsmobiledev.mobile_todolist

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val tasks: MutableList<Task>, private val context: Context) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBoxTask: CheckBox = itemView.findViewById(R.id.checkBoxTask)
        val textViewTaskDescription: TextView = itemView.findViewById(R.id.textViewTaskDescription)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteTaskButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.textViewTaskDescription.text = task.description
        holder.checkBoxTask.isChecked = task.isCompleted

        holder.checkBoxTask.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
        }

        holder.editButton.setOnClickListener{
            inputText { text ->
                task.description = text
                notifyDataSetChanged()
            }
        }

        holder.deleteButton.setOnClickListener{
            tasks.removeAt(position)
            notifyDataSetChanged()
        }

    }

    private fun inputText(callback: (String) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Enter text")

        val input = EditText(context)
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val text = input.text.toString()
            dialog.dismiss()
            callback(text)
        }

        builder.show()
    }

    override fun getItemCount() = tasks.size
}
