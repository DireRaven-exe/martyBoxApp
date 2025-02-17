package com.jetbrains.kmpapp.feature.messages.states

import com.jetbrains.kmpapp.feature.messages.StateMessage
import com.jetbrains.kmpapp.utils.MainUiState
import kotlinx.coroutines.flow.MutableStateFlow

class NothingMessage: StateMessage {
    override fun process(value: String, uiState: MutableStateFlow<MainUiState>): Boolean {
        return false
    }
}
