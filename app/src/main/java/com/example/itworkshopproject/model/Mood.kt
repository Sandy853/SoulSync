package com.example.itworkshopproject.model

import androidx.compose.ui.graphics.Color

enum class Mood(
    val emoji: String,
    val label: String,
    val color: Color
) {
    HAPPY("😊", "Happy", Color(0xFFFFD166)),
    SAD("😢", "Sad", Color(0xFF06AED5)),
    ANGRY("😡", "Angry", Color(0xFFEE4266)),
    TIRED("😴", "Tired", Color(0xFF9C89B8)),
    CALM("😌", "Calm", Color(0xFFB8F2E6)),
    ANXIOUS("😨", "Anxious", Color(0xFFAFCBFF)),
    EXCITED("🤩", "Excited", Color(0xFFFF9F1C)),
    NEUTRAL("😐", "Neutral", Color(0xFFE2E2E2))
}
