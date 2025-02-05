package com.jetbrains.kmpapp.feature.messages.states

import com.jetbrains.kmpapp.feature.messages.StateMessage
import com.jetbrains.kmpapp.utils.MainUiState
import com.jetbrains.kmpapp.utils.QueueUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class PlayingMessage: StateMessage {
    override fun processMain(value: String, uiState: MutableStateFlow<MainUiState>): Boolean {
        return try {
            val cleanedValue = value.replace("\"", "").trim()
            val isPlaying = cleanedValue.toBoolean()
            uiState.update { it.copy(isPlaying = isPlaying) }
            return true
        } catch (e: Exception) {
            Napier.e(tag = "WebSocket", message = "Error parsing isPlaying data: ${e.message}")
            false
        }
    }

    override fun processQueue(value: String, uiState: MutableStateFlow<QueueUiState>): Boolean {
        return try {
            val cleanedValue = value.replace("\"", "").trim()
            val isPlaying = cleanedValue.toBoolean()
            uiState.update { it.copy(isPlaying = isPlaying) }
            return true
        } catch (e: Exception) {
            Napier.e(tag = "WebSocket", message = "Error parsing isPlaying data: ${e.message}")
            false
        }
    }
}
