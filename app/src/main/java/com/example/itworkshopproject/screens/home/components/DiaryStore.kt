package com.example.itworkshopproject.screens.home.components

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple key-value storage for diary entries using SharedPreferences.
 * Each diary entry is saved with a date string as the key.
 */
object DiaryStore {

    private const val PREF_NAME = "diary_entries"  // SharedPreferences file name

    /**
     * Save the diary entry for a specific date.
     *
     * @param context Android context to access SharedPreferences
     * @param date Formatted date string (e.g., "08 Apr 2025")
     * @param entry The text entry to be saved
     */
    fun saveEntry(context: Context, date: String, entry: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(date, entry).apply()
    }

    /**
     * Retrieve the diary entry for a specific date.
     *
     * @param context Android context
     * @param date Formatted date string
     * @return The saved entry or empty string if none found
     */
    fun getEntry(context: Context, date: String): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(date, "") ?: ""
    }

    /**
     * Format a Date object to a readable string.
     *
     * @param date The Date object to format
     * @return A string formatted like "08 Apr 2025"
     */
    fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(date)
    }
}
