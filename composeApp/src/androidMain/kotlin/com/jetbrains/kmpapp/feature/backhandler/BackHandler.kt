package com.jetbrains.kmpapp.feature.backhandler

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun OnBackPressedHandler(isServerConnected: Boolean, isLoading: Boolean,
                                onBackFromLoading: () -> Unit,
                                onBackFromServerConnected: () -> Unit,
                                onBackDefault: () -> Unit

) {
    // Обработка кнопки назад на Android
    BackHandler {
        when {
            isLoading -> {
                onBackFromLoading() // Логика при загрузке
            }
            isServerConnected -> {
                onBackFromServerConnected() // Логика при подключении к серверу
            }
            else -> {
                onBackDefault() // Логика по умолчанию
            }
        }
    }
}