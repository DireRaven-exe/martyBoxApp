package com.jetbrains.kmpapp.feature.vibration

import androidx.compose.runtime.Composable
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

class IOSHapticFeedback : HapticFeedback {
    override fun performHapticFeedback(type: HapticFeedbackType) {
        val generator = when (type) {
            HapticFeedbackType.LongPress, HapticFeedbackType.Heavy ->
                UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
            HapticFeedbackType.Medium ->
                UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
            HapticFeedbackType.Light ->
                UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
        }
        generator.prepare()
        generator.impactOccurred()
    }
}

@Composable
actual fun provideHapticFeedback(): HapticFeedback = IOSHapticFeedback()