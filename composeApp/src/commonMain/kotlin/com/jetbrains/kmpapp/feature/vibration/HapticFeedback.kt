package com.jetbrains.kmpapp.feature.vibration

import androidx.compose.runtime.Composable

interface HapticFeedback {
    fun performHapticFeedback(type: HapticFeedbackType)
}

enum class HapticFeedbackType {
    LongPress, Light, Medium, Heavy
}

@Composable
expect fun provideHapticFeedback(): HapticFeedback