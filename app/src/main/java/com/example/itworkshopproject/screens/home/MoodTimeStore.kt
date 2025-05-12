package com.example.itworkshopproject.screens.home

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

private val Context.timeDataStore by preferencesDataStore(name = "mood_time_store")

object MoodTimeStore {
    private val LAST_SELECTION_TIME = longPreferencesKey("last_selection_time")
    private val SELECTION_COUNT = intPreferencesKey("selection_count")

    suspend fun canSelectMood(context: Context): Boolean {
        val prefs = context.timeDataStore.data.first()
        val lastTime = prefs[LAST_SELECTION_TIME] ?: 0L
        val count = prefs[SELECTION_COUNT] ?: 0
        val currentTime = System.currentTimeMillis()

        val tenMinutesAgo = currentTime - TimeUnit.MINUTES.toMillis(10)

        return if (lastTime < tenMinutesAgo) {
            // Reset time and count
            updateSelectionData(context, currentTime, 1)
            true
        } else if (count < 2) {
            updateSelectionData(context, lastTime, count + 1)
            true
        } else {
            false
        }
    }

    private suspend fun updateSelectionData(context: Context, time: Long, count: Int) {
        context.timeDataStore.edit { prefs ->
            prefs[LAST_SELECTION_TIME] = time
            prefs[SELECTION_COUNT] = count
        }
    }
}
