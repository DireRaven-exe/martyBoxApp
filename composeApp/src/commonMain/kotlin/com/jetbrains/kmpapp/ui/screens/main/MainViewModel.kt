package com.jetbrains.kmpapp.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.data.dto.models.Command
import com.jetbrains.kmpapp.data.dto.models.ResponseDto
import com.jetbrains.kmpapp.data.dto.models.toServerData
import com.jetbrains.kmpapp.data.dto.models.toSongs
import com.jetbrains.kmpapp.data.sockets.KtorWebsocketClient
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.feature.datastore.AppPreferencesRepository
import com.jetbrains.kmpapp.feature.messages.States
import com.jetbrains.kmpapp.utils.MainUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class MainViewModel(
    private val appPreferencesRepository: AppPreferencesRepository
) : ViewModel(), KtorWebsocketClient.WebsocketEvents {

    private val json = Json { ignoreUnknownKeys = true }

    private val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState = _mainUiState.asStateFlow()

    private var webSocketClient: KtorWebsocketClient? = null

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        _mainUiState.update { it.copy(isLoading = false, error = exception.message) }
    }

    init {
        getQrCode()
        getCurrentTable()
    }

    fun clearSavedQrCode() {
        viewModelScope.launch(coroutineExceptionHandler) {
            appPreferencesRepository.saveQrCode(qrCode = "")
        }
    }

    fun clearSavedTableNumber() =
        viewModelScope.launch(coroutineExceptionHandler) {
            appPreferencesRepository.saveTableNumber(tableNumber = -1)
        }

    fun getQrCode() = viewModelScope.launch(coroutineExceptionHandler) {
        appPreferencesRepository.getQrCode().collect { qrCode ->
            qrCode?.let {
                _mainUiState.update { it.copy(savedQrCode = qrCode) }
            }
        }
    }

    fun getCurrentTable() = viewModelScope.launch(coroutineExceptionHandler) {
        appPreferencesRepository.getTableNumber().collect { table ->
            table?.let {
                _mainUiState.update { it.copy(currentTable = table) }
            }
        }
    }

    fun saveCurrentTable(currentTable: Int) =
        viewModelScope.launch(coroutineExceptionHandler) {
            appPreferencesRepository.saveTableNumber(tableNumber = currentTable)
        }

    fun updateCurrentTable(tableNumber: Int) {
        _mainUiState.update { currentState ->
            currentState.copy(currentTable = tableNumber)
        }
    }

    private fun updateServerConnectionStatus(isServerConnected: Boolean) {
        _mainUiState.update { it.copy(isServerConnected = isServerConnected) }
    }

    fun connectToWebSocket(url: String) {
        webSocketClient = KtorWebsocketClient(
            url = url,
            listener = this
        )
        connect()
    }

    private fun connect() {
        viewModelScope.launch(Dispatchers.IO) {
            webSocketClient?.connect()
        }
    }

    override fun onReceive(data: String) {
        processWebSocketMessage(data)
    }

    override fun onConnected() {
        Napier.d(tag = "WebSocket", message = "Connected to WebSocket")
        updateServerConnectionStatus(true)
        sendCommandToServer(type = 17, value = "", table = 0)
    }

    override fun onDisconnected(reason: String) {
        Napier.d(tag = "WebSocket", message = "Disconnected: $reason")
        updateServerConnectionStatus(false)
    }

    private fun processWebSocketMessage(jsonString: String) {
        Napier.i(tag = "WebSocket", message = "Received json: $jsonString")
        val trimmedMessage = jsonString.trim()
        if (trimmedMessage == "stop") {
            clearSavedQrCode()
            clearSavedTableNumber()
            onDisconnected("Server was stopped")
            return
        }
        try {
            val jsonObject = json.parseToJsonElement(jsonString).jsonObject
            var isStateMessage = true
            var dataProcessed = false

            jsonObject.keys.forEach { key ->
                val value = jsonObject[key]
                val processed = States.getMessage(key).process(value?.toString() ?: "", _mainUiState)
                Napier.d(tag = "WebSocket", message = "Processed: $key: $processed")
                isStateMessage = isStateMessage && processed
                if (processed) dataProcessed = true
            }

            if (!isStateMessage) {
                if (jsonObject.containsKey("type") && jsonObject.containsKey("tables") && jsonObject.containsKey("value")) {
                    val responseData = json.decodeFromString<ResponseDto?>(jsonString)
                    Napier.d(tag = "WebSocket", message = "Response: $responseData")
                    responseData?.let {
                        dataProcessed = true
                        _mainUiState.value = _mainUiState.value.copy(
                            songs = it.toSongs(),
                            serverData = it.toServerData()
                        )
                    } ?: Napier.e(tag = "WebSocket", message = "Error: failed to parse full server state")
                } else {
                    Napier.w(tag = "WebSocket", message = "Received non-ResponseDto message, ignoring: $jsonString")
                }
            }

            // Если хоть что-то было обработано или пришли пустые данные, обновляем статус загрузки
            if (dataProcessed || jsonObject.isEmpty()) {
                updateIsLoading(isLoading = false)
            }
        } catch (e: Exception) {
            Napier.e(tag = "WebSocket", message = "Error parsing data: ${e.message}")
        }
    }

    fun sendCommandToServer(type: Int, value: String, table: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val command = json.encodeToString(Command(type, value, table))
                webSocketClient?.send(command)
            } catch (e: Exception) {
                Napier.e(tag = "WebSocket", message = "Failed to send command: ${e.message}")
            }
        }
    }

    fun updateCurrentSong(song: Song?) {
        _mainUiState.update { currentState ->
            currentState.copy(currentSong = song)
        }
    }

    fun updateTempo(tempo: Float) {
        _mainUiState.update { currentState ->
            currentState.copy(tempo = tempo)
        }
    }

    fun updatePitch(pitch: Int) {
        _mainUiState.update { currentState ->
            currentState.copy(pitch = pitch)
        }
    }

    fun updateAutoFullScreen(autoFullScreen: Boolean) {
        _mainUiState.update { currentState ->
            currentState.copy(autoFullScreen = autoFullScreen)
        }
    }

    fun updateAdaptatingTempo(adaptatingTempo: Boolean) {
        _mainUiState.update { currentState ->
            currentState.copy(adaptatingTempo = adaptatingTempo)
        }
    }
    fun updateDefaultVideoUse(defaultVideoUse: Boolean) {
        _mainUiState.update { currentState ->
            currentState.copy(defaultVideoUse = defaultVideoUse)
        }
    }

    fun updateSingingAssessment(singingAssessment: Boolean) {
        _mainUiState.update { currentState ->
            currentState.copy(singingAssessment = singingAssessment)
        }
    }

    fun updateSoundInPause(soundInPause: Boolean) {
        _mainUiState.update { currentState ->
            currentState.copy(soundInPause = soundInPause)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch(Dispatchers.IO) {
            webSocketClient?.stop()
        }
    }

    fun updateVolume(newVolume: Float) {
        _mainUiState.update { currentState ->
            currentState.copy(volume = newVolume)
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        _mainUiState.update { currentState ->
            currentState.copy(isLoading = isLoading)
        }
    }
}
