package com.jetbrains.kmpapp.ui.screens.main

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.data.dto.models.Command
import com.jetbrains.kmpapp.data.dto.models.ResponseDto
import com.jetbrains.kmpapp.data.dto.models.ResponseSongsForTabDto
import com.jetbrains.kmpapp.data.dto.models.toServerData
import com.jetbrains.kmpapp.data.dto.models.toSong
import com.jetbrains.kmpapp.data.dto.models.toSongInQueue
import com.jetbrains.kmpapp.data.sockets.WebSocketConnectionState
import com.jetbrains.kmpapp.data.sockets.WebSocketService
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.domain.models.SongInQueue
import com.jetbrains.kmpapp.domain.models.toSongInQueue
import com.jetbrains.kmpapp.feature.commands.BaseViewModel
import com.jetbrains.kmpapp.feature.datastore.AppPreferencesRepository
import com.jetbrains.kmpapp.feature.messages.States
import com.jetbrains.kmpapp.utils.MainUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val webSocketService: WebSocketService
) : BaseViewModel(appPreferencesRepository) {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    private val _currentTab = MutableStateFlow("")
    val currentTab = _currentTab.asStateFlow()

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages

    private val _connectionState = MutableStateFlow<WebSocketConnectionState>(
        WebSocketConnectionState.Disconnected
    )
    val connectionState: StateFlow<WebSocketConnectionState> = _connectionState

    init {
        Napier.e(tag = "AndroidWebSocket", message = "MainViewModel - Init - $this")
        observeWebSocket()
        getCurrentTable()
        getQrCode()
    }

    private fun observeWebSocket() {
        // Подписка на состояние подключения
        viewModelScope.launch {
            this@MainViewModel.webSocketService.observeConnectionState().collect { state ->
                _connectionState.value = state
                if (state == WebSocketConnectionState.Connected) {
                    Napier.e(tag = "AndroidWebSocket", message = "count = $$$")
                    onConnected()  // Вызываем onConnected, когда WebSocket подключен
                }
                updateServerConnectionStatus(state == WebSocketConnectionState.Connected)
            }
        }

        // Подписка на получение сообщений
        viewModelScope.launch {
            this@MainViewModel.webSocketService.observeMessages().collect { message ->
                onReceive(message)  // Вызываем onReceived при получении сообщения
                _messages.value += message
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _uiState.value = MainUiState()
        disconnect()
    }

    override fun updateSongsForTab(tabName: String) {
        viewModelScope.launch {
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val command = json.encodeToString(Command(type, value, table))
                webSocketService.sendMessage(command)
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

    fun disconnect() {
        this@MainViewModel.webSocketService.disconnect()
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
        Napier.e(tag = "WebSocket", message = "Connecting to WebSocket: $url")
        webSocketService.connect(url)
    }

    override fun onReceive(data: String) {
        processWebSocketMessage(data)
    }

    override fun onConnected() {
        Napier.d(tag = "WebSocket", message = "Connected to WebSocket")
        updateServerConnectionStatus(true)
        sendCommand(type = 26, value = "[\"tablist\"]", table = _uiState.value.currentTable)

    }

    override fun onDisconnected(reason: String) {
        _connectionState.value = WebSocketConnectionState.Disconnected
        Napier.d(tag = "WebSocket", message = "Disconnected: $reason")
        updateServerConnectionStatus(false)
    }

    override fun onPingMessage() {
        sendCommand(type = 26, value = "[\"playlist\"]", table = _uiState.value.currentTable)
        Napier.d(tag = "AndroidWebSocket", message = "isLoading = ${_uiState.value.isLoading} | isTabLoading = ${_uiState.value.isTabLoading}")
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
                val processed = States.getMessage(key).process(value?.toString() ?: "", _uiState)
                isStateMessage = isStateMessage && processed
                if (processed) dataProcessed = true
            }

            if (!isStateMessage) {
                if (jsonObject.containsKeys("type", "tables", "tabs")) {
                    try {
                        val responseData = json.decodeFromString<ResponseDto>(jsonString)
                        dataProcessed = true
                        _uiState.value = _uiState.value.copy(serverData = responseData.toServerData())
                        _currentTab.value = responseData.tabs[0]
                        sendCommand(type = 17, value = "", table = _uiState.value.currentTable)
                        sendCommand(type = 1, value = "", table = _uiState.value.currentTable)
                    } catch (e: Exception) {
                        Napier.e(tag = "WebSocket", message = "Error parsing ResponseDto: ${e.message}")
                    }
                } else {
                    if (jsonObject.containsKeys("last", "songs", "tab", "isBase")) {
                        val responseData = json.decodeFromString<ResponseSongsForTabDto>(jsonString)
                        dataProcessed = true

                        if (responseData.isBase) {
                            val updatedSongs = responseData.songs.map { song ->
                                song.copy(tab = responseData.tab).toSong()
                            }
                            _uiState.update { state ->
                                state.copy(
                                    songs = state.songs.apply { addAll(updatedSongs) },
                                    isLoading = !responseData.last
                                )
                            }
                            Napier.d(tag = "AndroidWebSocket", message = "isLoading = ${_uiState.value.isLoading}\nisTabLoading = ${_uiState.value.isTabLoading}")
                            if (responseData.last) {
                                _uiState.update { state ->
                                    state.copy(
                                        isTabLoading = !responseData.last,
                                        currentSongs = state.songs.filter { it.tab == _currentTab.value }
                                    )
                                }
                            }
                        } else {
                            val updatedSongs = responseData.songs.map { song ->
                                song.copy(tab = responseData.tab).toSongInQueue()
                            }
                            _uiState.update { state ->
                                state.copy(
                                    currentPlaylist = updatedSongs.toMutableList(),
                                )
                            }
                        }

                    }
                    Napier.w(tag = "WebSocket", message = "Received unexpected message format: $jsonString")
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

    private fun JsonObject.containsKeys(vararg keys: String): Boolean {
        return keys.all { this.containsKey(it) }
    }

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
        _uiState.update { state ->
            state.copy(currentPlaylist = emptyList<SongInQueue>().toMutableList())
        }
        onPingMessage()
    }

    override fun updateSoundInPause(soundInPause: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(soundInPause = soundInPause)
        }
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
        if (oldIndex == newIndex) return

        val updatedSongs = _uiState.value.currentPlaylist.toMutableList()
        val song = updatedSongs.removeAt(oldIndex)
        updatedSongs.add(newIndex, song)

        _uiState.update { currentState ->
            currentState.copy(currentPlaylist = updatedSongs)
        }
    }

    override fun clearQueue() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(currentPlaylist = mutableListOf())
            }
        }
    }
}

