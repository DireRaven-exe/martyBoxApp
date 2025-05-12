package com.jetbrains.kmpapp.feature.messages.states

import com.jetbrains.kmpapp.feature.messages.StateMessage
import com.jetbrains.kmpapp.utils.MainUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class PlayingOrderMessage: StateMessage {
    override fun process(value: String, uiState: MutableStateFlow<MainUiState>): Boolean {
        return try {
            val cleanedValue = value.replace("\"", "").trim()
            val playingOrder = cleanedValue.toBoolean()
            Napier.e(tag = "WebSocket", message = playingOrder.toString())
            uiState.update { it.copy(playingOrder = playingOrder) }
            return true
        } catch (e: Exception) {
            Napier.e(tag = "WebSocket", message = "Error parsing playingOrder data: ${e.message}")
            false
        }
    }
}
