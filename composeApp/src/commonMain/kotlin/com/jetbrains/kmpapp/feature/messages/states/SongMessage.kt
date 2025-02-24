package com.jetbrains.kmpapp.feature.messages.states

import com.jetbrains.kmpapp.data.dto.models.SongDto
import com.jetbrains.kmpapp.data.dto.models.toSong
import com.jetbrains.kmpapp.feature.messages.StateMessage
import com.jetbrains.kmpapp.utils.MainUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json

class SongMessage: StateMessage {
    private val json = Json { ignoreUnknownKeys = true }

    override fun process(value: String, uiState: MutableStateFlow<MainUiState>): Boolean {
        return try {
            val cleanedValue = value.trim().removePrefix("\"").removeSuffix("\"").replace("\\\"", "\"")
            val songDto = json.decodeFromString<SongDto>(cleanedValue)

            songDto.let {
                uiState.update { it.copy(currentSong = songDto.toSong()) }
            } ?: Napier.e(tag = "WebSocket", message = "Error: failed to parse song data")
            true
        } catch (e: Exception) {
            Napier.e(tag = "WebSocket", message = "Error parsing song data: ${e.message}")
            false
        }
    }
}
