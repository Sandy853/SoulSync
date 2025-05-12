package com.example.itworkshopproject.screens.home

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object NoteDataStore {
    private const val FILE_NAME = "student_notes.json"

    suspend fun saveNotes(context: Context, notes: List<Note>) {
        withContext(Dispatchers.IO) {
            val file = File(context.filesDir, FILE_NAME)
            file.writeText(Json.encodeToString(notes))
        }
    }

    fun loadNotes(context: Context): List<Note> {
        return try {
            val file = File(context.filesDir, FILE_NAME)
            if (file.exists()) {
                Json.decodeFromString(file.readText())
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
