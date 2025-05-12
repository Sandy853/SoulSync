package com.example.itworkshopproject.screens.home.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "mood_counts")
