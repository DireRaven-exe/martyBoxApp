package com.jetbrains.kmpapp.feature.messages.states

import com.jetbrains.kmpapp.feature.messages.StateMessage
import com.jetbrains.kmpapp.utils.MainUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SingingAssessmentMessage: StateMessage {
    override fun process(value: String, uiState: MutableStateFlow<MainUiState>): Boolean {
        return try {
            val cleanedValue = value.replace("\"", "").trim()
            val singingAssessment = cleanedValue.toBoolean()
            uiState.update { it.copy(singingAssessment = singingAssessment) }
            true
        } catch (e: Exception) {
            Napier.e(tag = "WebSocket", message = "Error parsing singingAssessment data: ${e.message}")
            false
        }
    }
}
