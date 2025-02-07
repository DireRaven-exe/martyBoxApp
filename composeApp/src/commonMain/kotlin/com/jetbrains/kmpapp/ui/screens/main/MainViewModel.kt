package com.jetbrains.kmpapp.ui.screens.main

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.data.dto.models.Command
import com.jetbrains.kmpapp.data.dto.models.ResponseDto
import com.jetbrains.kmpapp.data.dto.models.toServerData
import com.jetbrains.kmpapp.data.dto.models.toSongs
import com.jetbrains.kmpapp.data.sockets.WebSocketManager
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.domain.models.SongInQueue
import com.jetbrains.kmpapp.domain.models.toSongInQueue
import com.jetbrains.kmpapp.feature.commands.BaseViewModel
import com.jetbrains.kmpapp.feature.datastore.AppPreferencesRepository
import com.jetbrains.kmpapp.feature.messages.States
import com.jetbrains.kmpapp.utils.MainUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

@Immutable
class MainViewModel(
    appPreferencesRepository: AppPreferencesRepository,
) : BaseViewModel(appPreferencesRepository) {

    private val _uiState = MutableStateFlow(MainUiState())
    val mainUiState = _uiState.asStateFlow()

    private val webSocketManager = WebSocketManager.getInstance()

    init {
        webSocketManager.addListener(this)
        getQrCode()
        getCurrentTable()
    }

    override fun updateSongsForTab(tabName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                currentSongs = emptyList(),
                isTabLoading = true
            )
            val filteredSongs = _uiState.value.songs.filter { it.tab == tabName }
            _uiState.value = _uiState.value.copy(
                currentSongs = filteredSongs,
                isTabLoading = false
            )
        }
    }

    override fun updateError(error: String?) {
        _uiState.update { it.copy(error = error) }
    }

    override fun addSongToQueue(song: Song) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(currentPlaylist = currentState.currentPlaylist.apply { add(song.toSongInQueue()) })
            }
        }

    }

    override fun removeSong(songInQueue: SongInQueue) {
        val updatedPlaylist = _uiState.value.currentPlaylist.toMutableList()
        updatedPlaylist.remove(songInQueue)
        _uiState.update { state ->
            state.copy(currentPlaylist = updatedPlaylist)
        }
    }

    override fun sendCommand(type: Int, value: String, table: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val command = json.encodeToString(Command(type, value, table))
                webSocketManager.send(command)
            } catch (e: Exception) {
                Napier.e(tag = "WebSocket", message = "Failed to send command: ${e.message}")
            }
        }
    }

    fun getQrCode() = viewModelScope.launch(coroutineExceptionHandler) {
        appPreferencesRepository.getQrCode().collect { qrCode ->
            qrCode?.let {
                _uiState.update { it.copy(savedQrCode = qrCode) }
            }
        }
    }

    fun getCurrentTable() = viewModelScope.launch(coroutineExceptionHandler) {
        appPreferencesRepository.getTableNumber().collect { table ->
            table?.let { _uiState.update { it.copy(currentTable = table) } }
        }
    }

    fun updateCurrentTable(tableNumber: Int) {
        _uiState.update { currentState ->
            currentState.copy(currentTable = tableNumber)
        }
    }

    private fun updateServerConnectionStatus(isServerConnected: Boolean) {
        _uiState.update { it.copy(isServerConnected = isServerConnected) }
    }

    fun connectToWebSocket(url: String) {
        webSocketManager.connect(url)
    }

    override fun onReceive(data: String) {
        processWebSocketMessage(data)
    }

    override fun onConnected() {
        Napier.d(tag = "WebSocket", message = "Connected to WebSocket")
        updateServerConnectionStatus(true)
        sendCommand(type = 17, value = "", table = 0)
        sendCommand(type = 26, value = "[\"playlist\"]", table = _uiState.value.currentTable)
    }

    override fun onDisconnected(reason: String) {
        Napier.d(tag = "WebSocket", message = "Disconnected: $reason")
        updateServerConnectionStatus(false)
    }

    override fun onPingMessage() {
        sendCommand(type = 26, value = "[\"playlist\"]", table = _uiState.value.currentTable)
    }

    override fun processWebSocketMessage(jsonString: String) {
        val trimmedMessage = jsonString.trim()
        if (trimmedMessage == "stop") {
            clearSavedQrCode()
            clearSavedTableNumber()
            onDisconnected("Server was stopped")
            return
        }

        try {
            val jsonObject = json.tryParseToJsonElement(jsonString)?.jsonObject ?: return

            var isStateMessage = true
            var dataProcessed = false

            for ((key, value) in jsonObject) {
                val processed = States.getMessage(key).processMain(value?.toString() ?: "", _uiState)
                isStateMessage = isStateMessage && processed
                if (processed) dataProcessed = true
            }

            if (!isStateMessage && jsonObject.containsKeys("type", "tables", "value")) {
                try {
                    val responseData = json.decodeFromString<ResponseDto>(jsonString)
                    dataProcessed = true
                    _uiState.value = _uiState.value.copy(
                        songs = responseData.toSongs(),
                        serverData = responseData.toServerData()
                    )
                } catch (e: Exception) {
                    Napier.e(tag = "WebSocket", message = "Error parsing ResponseDto: ${e.message}")
                }
            } else {
                Napier.w(tag = "WebSocket", message = "Received unexpected message format: $jsonString")
            }

            if (dataProcessed || jsonObject.isEmpty()) {
                updateIsLoading(isLoading = false)
            }
        } catch (e: Exception) {
            Napier.e(tag = "WebSocket", message = "Error parsing data: ${e.message}")
        }
    }

    /**
     * Проверяет наличие всех ключей в JSON
     */
    private fun JsonObject.containsKeys(vararg keys: String): Boolean {
        return keys.all { this.containsKey(it) }
    }

    /**
     * Попытка парсинга JSON с обработкой ошибок
     */
    private fun Json.tryParseToJsonElement(jsonString: String): JsonElement? {
        return try {
            parseToJsonElement(jsonString)
        } catch (e: Exception) {
            Napier.e(tag = "JSON", message = "Failed to parse JSON: ${e.message}")
            null
        }
    }

    override fun updateCurrentSong(song: Song?) {
        _uiState.update { currentState ->
            currentState.copy(currentSong = song)
        }
    }

    override fun updateTempo(tempo: Float) {
        _uiState.update { currentState ->
            currentState.copy(tempo = tempo)
        }
    }

    override fun updatePitch(pitch: Int) {
        _uiState.update { currentState ->
            currentState.copy(pitch = pitch)
        }
    }

    override fun updateAutoFullScreen(autoFullScreen: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(autoFullScreen = autoFullScreen)
        }
    }

    override fun updateSingingAssessment(singingAssessment: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(singingAssessment = singingAssessment)
        }
    }

    override fun updateQueue() {
        onPingMessage()
    }

    override fun updateSoundInPause(soundInPause: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(soundInPause = soundInPause)
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.removeListener(this)
    }

    override fun updateVolume(volume: Float) {
        _uiState.update { currentState ->
            currentState.copy(volume = volume)
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isLoading = isLoading)
        }
    }

    override fun moveSongInQueue(oldIndex: Int, newIndex: Int) {
        if (oldIndex == newIndex) return  // Исключаем ненужные обновления

        val updatedSongs = _uiState.value.currentPlaylist.toMutableList()
        val song = updatedSongs.removeAt(oldIndex)
        updatedSongs.add(newIndex, song)

        _uiState.update { currentState ->
            currentState.copy(currentPlaylist = updatedSongs) // Обновляем только список
        }
    }
}
