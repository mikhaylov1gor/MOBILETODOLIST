package com.hitsmobiledev.mobile_todolist

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream


data class Task(var description: String, var isCompleted: Boolean)

class MainActivity : AppCompatActivity() {
    private val permissionCode = 123;

    private lateinit var taskAdapter: TaskAdapter
    private val tasks = mutableListOf<Task>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        val recyclerViewTasks: RecyclerView = findViewById(R.id.taskView)
        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(tasks,this)
        recyclerViewTasks.adapter = taskAdapter


        val addTaskButton: ImageButton = findViewById(R.id.addTaskButton)
        addTaskButton.setOnClickListener{
            addTask()
        }

        val saveTasksButton: ImageButton = findViewById(R.id.saveTasksButton)
        saveTasksButton.setOnClickListener{
            saveTasks()
        }

        val deleteTasksButton: ImageButton = findViewById(R.id.deleteTasksButton)
        deleteTasksButton.setOnClickListener{
            deleteTasks()
        }

        val loadTasksButton: ImageButton = findViewById(R.id.loadTasksButton)
        loadTasksButton.setOnClickListener{
            loadTasks()
        }
    }

    private fun addTask(){
        inputText { text ->
            val newTask = Task(text, false)
            tasks.add(newTask)
            taskAdapter.notifyItemInserted(tasks.size - 1)
        }
    }
    // сохранение и запись файла взял с прошлогодового проекта, допилил оверстэком и чатом джипити
    private fun saveTasks() {
        inputText { fileName ->
            val gson = Gson()
            val tasksJson = gson.toJson(tasks)

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (downloadsDir != null) {
                val file = File(downloadsDir, "$fileName.json")
                try {
                    FileOutputStream(file).use { fos ->
                        fos.write(tasksJson.toByteArray())
                    }
                    Toast.makeText(this, "Сохранено как $fileName.json", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Ошибка при сохранении", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadTasks() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/json"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, permissionCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == permissionCode && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val tasksJson = inputStream?.bufferedReader().use { it?.readText() }

                    val gson = Gson()
                    val taskListType = object : TypeToken<MutableList<Task>>() {}.type
                    val loadedTasks: MutableList<Task> = gson.fromJson(tasksJson, taskListType)

                    tasks.clear()
                    tasks.addAll(loadedTasks)
                    taskAdapter.notifyDataSetChanged()

                    Toast.makeText(this, "Задачи загружены", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Ошибка при загрузке задач", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }


    private fun deleteTasks(){
        tasks.clear()
        taskAdapter.notifyDataSetChanged()
    }

    private fun inputText(callback: (String) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter text")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val text = input.text.toString()
            dialog.dismiss()
            callback(text)
        }

        builder.show()
    }
}