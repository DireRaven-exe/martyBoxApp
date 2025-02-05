package com.jetbrains.kmpapp.feature.messages.states

import com.jetbrains.kmpapp.feature.messages.StateMessage
import com.jetbrains.kmpapp.utils.MainUiState
import com.jetbrains.kmpapp.utils.QueueUiState
import kotlinx.coroutines.flow.MutableStateFlow

class NothingMessage: StateMessage {
    override fun processMain(value: String, uiState: MutableStateFlow<MainUiState>): Boolean {
        return false
    }

    override fun processQueue(value: String, uiState: MutableStateFlow<QueueUiState>): Boolean {
        return false
    }
}
