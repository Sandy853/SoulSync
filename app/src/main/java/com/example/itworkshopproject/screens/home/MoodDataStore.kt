package com.example.itworkshopproject.screens.home

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.itworkshopproject.model.Mood
import com.example.itworkshopproject.screens.home.components.dataStore
import kotlinx.coroutines.flow.first

object MoodDataStore {

    suspend fun incrementMoodCount(context: Context, mood: Mood) {
        val key = intPreferencesKey(mood.name)
        context.dataStore.edit { prefs ->
            val current = prefs[key] ?: 0
            prefs[key] = current + 1
        }
    }

    suspend fun getMoodCounts(context: Context): Map<Mood, Int> {
        val prefs = context.dataStore.data.first()
        val result = mutableMapOf<Mood, Int>()
        Mood.values().forEach { mood ->
            val key = intPreferencesKey(mood.name)
            result[mood] = prefs[key] ?: 0
        }
        return result
    }
}
