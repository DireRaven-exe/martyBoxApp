package com.jetbrains.kmpapp.feature.messages.states

import com.jetbrains.kmpapp.feature.messages.StateMessage
import com.jetbrains.kmpapp.utils.MainUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class PitchMessage: StateMessage {
    override fun process(value: String, uiState: MutableStateFlow<MainUiState>): Boolean {
        return try {
            val cleanedValue = value.replace("\"", "").trim()
            val pitch = cleanedValue.toInt()
            uiState.update { it.copy(pitch = pitch) }
            Napier.i(tag = "WebSocket", message = "Success parsing pitch data: ${uiState.value.pitch}")
            true
        } catch (e: Exception) {
            Napier.e(tag = "WebSocket", message = "Error parsing pitch data: ${e.message}")
            false
        }
    }
}
