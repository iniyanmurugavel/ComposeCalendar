package com.example.myapplication

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

class CalendarUI(
    val availableSessions: List<LocalDate>
)

class DayColors(
    val text: Color,
    val background: Color,
    val dot: Color,
    val border: Color,
    val focus: Color
)