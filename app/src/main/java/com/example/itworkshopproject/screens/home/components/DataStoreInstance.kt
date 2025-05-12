package com.example.itworkshopproject.screens.home.components

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// ✅ This defines the DataStore as an extension property on Context
val Context.dataStore by preferencesDataStore(name = "mood_preferences")
