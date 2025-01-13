package com.jetbrains.kmpapp.feature.backhandler

import androidx.compose.runtime.Composable

@Composable
actual fun OnBackPressedHandler(isServerConnected: Boolean, isLoading: Boolean,
                                onBackFromLoading: () -> Unit,
                                onBackFromServerConnected: () -> Unit,
                                onBackDefault: () -> Unit) {
    // Для iOS можно ничего не делать или просто скрыть кнопку назад
    // или обработать в SwiftUI
}