package com.example.myapplication

import androidx.compose.ui.graphics.Color

fun Color?.orTransparent() = this ?: Color.Transparent
