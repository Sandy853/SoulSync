package com.example.itworkshopproject.screens.home

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

@Serializable
data class TaskListWrapper(val tasks: List<Task>)

val Context.taskDataStore: DataStore<TaskListWrapper> by dataStore(
    fileName = "tasks.json",
    serializer = object : androidx.datastore.core.Serializer<TaskListWrapper> {
        override val defaultValue: TaskListWrapper = TaskListWrapper(emptyList())

        override suspend fun readFrom(input: java.io.InputStream): TaskListWrapper {
            return try {
                Json.decodeFromString(
                    TaskListWrapper.serializer(),
                    input.readBytes().decodeToString()
                )
            } catch (e: Exception) {
                defaultValue
            }
        }

        override suspend fun writeTo(t: TaskListWrapper, output: java.io.OutputStream) {
            output.write(Json.encodeToString(TaskListWrapper.serializer(), t).encodeToByteArray())
        }
    }
)

class TaskDataStore(private val context: Context) {
    val taskFlow: Flow<List<Task>> = context.taskDataStore.data.map { it.tasks }

    suspend fun saveTasks(tasks: List<Task>) {
        context.taskDataStore.updateData { TaskListWrapper(tasks) }
    }
}
