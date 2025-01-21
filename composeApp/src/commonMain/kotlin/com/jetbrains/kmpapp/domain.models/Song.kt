package com.jetbrains.kmpapp.domain.models

import androidx.compose.runtime.Immutable

@Immutable
data class Song(
    val artist: String,
    val tab: String,
    val id: Int,
    val title: String
)
