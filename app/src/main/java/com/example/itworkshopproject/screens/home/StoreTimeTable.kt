package com.example.itworkshopproject.screens.home

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

private val Context.timeTableDataStore by preferencesDataStore(name = "time_table_store")

object StoreTimeTable {
    private val TIME_SLOTS_KEY = stringPreferencesKey("time_slots")

    suspend fun saveTimeSlots(context: Context, timeSlots: List<TimeSlot>) {
        context.timeTableDataStore.edit { preferences ->
            val json = Gson().toJson(timeSlots)
            preferences[TIME_SLOTS_KEY] = json
        }
    }

    suspend fun loadTimeSlots(context: Context): List<TimeSlot> {
        val preferences = context.timeTableDataStore.data.first()
        val json = preferences[TIME_SLOTS_KEY] ?: return emptyList()

        val type = object : TypeToken<List<TimeSlot>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }

    suspend fun clearTimeSlots(context: Context) {
        context.timeTableDataStore.edit { preferences ->
            preferences.remove(TIME_SLOTS_KEY)
        }
    }
}