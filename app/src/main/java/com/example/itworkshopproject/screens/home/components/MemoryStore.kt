package com.example.itworkshopproject.screens.home.components

import android.content.Context
import com.example.itworkshopproject.screens.home.MemoryEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MemoryStore {
    private const val PREF_NAME = "memory_entries"
    private const val KEY_ENTRIES = "entries"

    private val gson = Gson()

    fun saveEntries(context: Context, entries: List<MemoryEntry>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(entries)
        prefs.edit().putString(KEY_ENTRIES, json).apply()
    }

    fun getEntries(context: Context): List<MemoryEntry> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_ENTRIES, null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<MemoryEntry>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
