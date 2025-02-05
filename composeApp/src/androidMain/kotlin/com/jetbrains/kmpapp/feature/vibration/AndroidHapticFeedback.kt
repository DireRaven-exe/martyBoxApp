package com.jetbrains.kmpapp.feature.vibration

import android.app.Activity
import android.content.Context
import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

class AndroidHapticFeedback(private val context: Context) : HapticFeedback {
    override fun performHapticFeedback(type: HapticFeedbackType) {
        val windowView = (context as? Activity)?.window?.decorView ?: return
        val feedbackType = when (type) {
            HapticFeedbackType.LongPress -> HapticFeedbackConstants.LONG_PRESS
            HapticFeedbackType.Light -> HapticFeedbackConstants.KEYBOARD_TAP
            HapticFeedbackType.Medium -> HapticFeedbackConstants.VIRTUAL_KEY
            HapticFeedbackType.Heavy -> HapticFeedbackConstants.CONTEXT_CLICK
            else -> HapticFeedbackConstants.LONG_PRESS
        }
        windowView.performHapticFeedback(feedbackType)
    }
}

@Composable
actual fun provideHapticFeedback(): HapticFeedback {
    val context = LocalContext.current
    return AndroidHapticFeedback(context)
}