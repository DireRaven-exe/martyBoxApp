package com.jetbrains.kmpapp.feature.messages

import com.jetbrains.kmpapp.utils.MainUiState
import com.jetbrains.kmpapp.utils.QueueUiState
import kotlinx.coroutines.flow.MutableStateFlow

interface StateMessage {
    fun processMain(value: String, uiState: MutableStateFlow<MainUiState>): Boolean

    fun processQueue(value: String, uiState: MutableStateFlow<QueueUiState>): Boolean
}
