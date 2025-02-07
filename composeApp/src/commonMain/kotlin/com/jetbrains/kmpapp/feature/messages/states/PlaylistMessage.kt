package com.jetbrains.kmpapp.feature.messages.states

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.jetbrains.kmpapp.data.dto.models.SongsDto
import com.jetbrains.kmpapp.data.dto.models.toSongInQueue
import com.jetbrains.kmpapp.data.dto.models.toSongs
import com.jetbrains.kmpapp.domain.models.SongInQueue
import com.jetbrains.kmpapp.domain.models.toSongInQueue
import com.jetbrains.kmpapp.feature.messages.StateMessage
import com.jetbrains.kmpapp.utils.MainUiState
import com.jetbrains.kmpapp.utils.QueueUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray

class PlaylistMessage : StateMessage {
    private val json = Json { ignoreUnknownKeys = true }

    override fun processMain(value: String, uiState: MutableStateFlow<MainUiState>): Boolean {
        return try {
            // Убираем лишние внешние кавычки и экранированные символы
            val cleanedValue = value.trim().removeSurrounding("\"").replace("\\\"", "\"")

            // Декодируем массив JSON
            val playlists = json.decodeFromString<List<JsonObject>>(cleanedValue)

            // Извлекаем список песен из "playlist"
            val songs = playlists.flatMap { playlist ->
                playlist[""]?.jsonArray?.mapNotNull { songJson ->
                    try {
                        val songDto = json.decodeFromJsonElement<SongsDto>(songJson)
                        songDto.toSongInQueue()
                    } catch (e: Exception) {
                        Napier.e(tag = "WebSocket", message = "Error decoding song: ${e.message}")
                        null
                    }
                } ?: emptyList()
            }

            // Обновляем состояние UI
            uiState.update { currentState ->
                currentState.copy(currentPlaylist = songs.toMutableList())
            }
            true
        } catch (e: Exception) {
            Napier.e(tag = "WebSocket", message = "Error parsing playlist data: ${e.message}")
            false
        }
    }

    override fun processQueue(value: String, uiState: MutableStateFlow<QueueUiState>): Boolean {
        return try {
            // Убираем лишние внешние кавычки и экранированные символы
            val cleanedValue = value.trim().removeSurrounding("\"").replace("\\\"", "\"")

            // Декодируем массив JSON
            val playlists = json.decodeFromString<List<JsonObject>>(cleanedValue)

            // Извлекаем список песен из "playlist"
            val songs = playlists.flatMap { playlist ->
                playlist[""]?.jsonArray?.mapNotNull { songJson ->
                    try {
                        val songDto = json.decodeFromJsonElement<SongsDto>(songJson)
                        songDto.toSongs()
                    } catch (e: Exception) {
                        Napier.e(tag = "WebSocket", message = "Error decoding song: ${e.message}")
                        null
                    }
                } ?: emptyList()
            }

            // Обновляем состояние UI
            uiState.update { currentState ->
                currentState.copy(currentPlaylist = SnapshotStateList<SongInQueue>().apply { addAll(songs.map { it.toSongInQueue() }) })
            }
            Napier.e(tag = "currentPlaylist", message = "currentPlaylist = ${uiState.value.currentPlaylist}")
            true
        } catch (e: Exception) {
            Napier.e(tag = "WebSocket", message = "Error parsing playlist data: ${e.message}")
            false
        }
    }
}