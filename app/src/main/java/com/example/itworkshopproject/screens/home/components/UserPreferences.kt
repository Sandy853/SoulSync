package com.example.itworkshopproject.screens.home.components

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "user_prefs"
private const val KEY_NICKNAME = "nickname"

// Save nickname to SharedPreferences
fun saveNickname(context: Context, nickname: String) {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(KEY_NICKNAME, nickname).apply()
}

// Retrieve nickname from SharedPreferences
fun getNickname(context: Context): String? {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString(KEY_NICKNAME, null)
}
