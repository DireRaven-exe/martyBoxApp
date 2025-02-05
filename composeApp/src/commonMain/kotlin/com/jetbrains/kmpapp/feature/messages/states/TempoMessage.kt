package com.jetbrains.kmpapp.feature.messages.states

import com.jetbrains.kmpapp.feature.messages.StateMessage
import com.jetbrains.kmpapp.utils.MainUiState
import com.jetbrains.kmpapp.utils.QueueUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class TempoMessage: StateMessage {
    override fun processMain(value: String, uiState: MutableStateFlow<MainUiState>): Boolean {
        return try {
            val cleanedValue = value.replace("\"", "").trim()
            val tempo = cleanedValue.toFloat()
            uiState.update { it.copy(tempo = tempo) }
            true
        } catch (e: Exception) {
            Napier.e(tag = "WebSocket", message = "Error parsing tempo data: ${e.message}")
            false
        }
    }

    override fun processQueue(value: String, uiState: MutableStateFlow<QueueUiState>): Boolean {
        return try {
            val cleanedValue = value.replace("\"", "").trim()
            val tempo = cleanedValue.toFloat()
            uiState.update { it.copy(tempo = tempo) }
            true
        } catch (e: Exception) {
            Napier.e(tag = "WebSocket", message = "Error parsing tempo data: ${e.message}")
            false
        }
    }
}
