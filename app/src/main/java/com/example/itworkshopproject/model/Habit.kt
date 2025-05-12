package com.example.itworkshopproject.model

@kotlinx.serialization.Serializable
data class Habit(
    val title: String,
    val isGood: Boolean = true,
    val currentStreak: Int = 0,
    val lastCompleted: Long = 0L,
    val duration: String = "7 Days",

    val createdDate: Long = System.currentTimeMillis()
)
