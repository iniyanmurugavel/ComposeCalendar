package com.example.myapplication

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val CIRCLE_FOCUS_SIZE = 44.dp
private val CIRCLE_BORDERS_SIZE = 40.dp

@SuppressLint("ModifierFactoryUnreferencedReceiver")
internal fun Modifier.focusCircle(focusColor: Color) = size(CIRCLE_FOCUS_SIZE)
    .border(2.dp, focusColor, CircleShape)
    .clip(CircleShape)
    .padding(4.dp)

@SuppressLint("ModifierFactoryUnreferencedReceiver")
internal fun Modifier.borderCircle(borderColor: Color) = size(CIRCLE_BORDERS_SIZE)
    .border(2.dp, borderColor, CircleShape)
    .clip(CircleShape)