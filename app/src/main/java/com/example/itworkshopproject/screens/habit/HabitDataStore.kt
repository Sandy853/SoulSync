package com.example.itworkshopproject.screens.habit

import android.content.Context
import android.util.Log
import com.example.itworkshopproject.model.Habit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object HabitDataStore {

    private const val HABIT_FILE_NAME = "habits.json"
    private val gson = Gson()

    fun saveHabits(context: Context, habits: List<Habit>) {
        try {
            val json = gson.toJson(habits)
            val file = File(context.filesDir, HABIT_FILE_NAME)
            file.writeText(json)
            Log.d("HabitDataStore", "Habits saved successfully 😘")
        } catch (e: Exception) {
            Log.e("HabitDataStore", "Failed to save habits 💔", e)
        }
    }

    fun loadHabits(context: Context): List<Habit> {
        return try {
            val file = File(context.filesDir, HABIT_FILE_NAME)
            if (file.exists()) {
                val json = file.readText()
                val type = object : TypeToken<List<Habit>>() {}.type
                gson.fromJson<List<Habit>>(json, type) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("HabitDataStore", "Failed to load habits 😢", e)
            emptyList()
        }
    }
}
