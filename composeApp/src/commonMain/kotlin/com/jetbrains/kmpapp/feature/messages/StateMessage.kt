package com.jetbrains.kmpapp.feature.messages

import com.jetbrains.kmpapp.utils.MainUiState
import kotlinx.coroutines.flow.MutableStateFlow

interface StateMessage {
    fun process(value: String, uiState: MutableStateFlow<MainUiState>): Boolean
}
