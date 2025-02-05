package com.jetbrains.kmpapp.ui.screens.queue

import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.data.dto.models.ResponseDto
import com.jetbrains.kmpapp.data.dto.models.toServerData
import com.jetbrains.kmpapp.data.dto.models.toSongsInQueue
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.domain.models.SongInQueue
import com.jetbrains.kmpapp.domain.models.toSong
import com.jetbrains.kmpapp.domain.models.toSongInQueue
import com.jetbrains.kmpapp.feature.commands.BaseViewModel
import com.jetbrains.kmpapp.feature.datastore.AppPreferencesRepository
import com.jetbrains.kmpapp.feature.datastore.QueueRepository
import com.jetbrains.kmpapp.feature.messages.States
import com.jetbrains.kmpapp.utils.QueueUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonObject

class QueueViewModel(
    appPreferencesRepository: AppPreferencesRepository,
    //val webSocketClient: KtorWebsocketClient,
    private val queueRepository: QueueRepository
) : BaseViewModel(appPreferencesRepository) {

    private val _uiState = MutableStateFlow(QueueUiState())
    val uiState = _uiState
        .onStart {
            getQrCode()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = QueueUiState()
        )

    init {
        getQrCode()
        getCurrentTable()
        updateSongs()
    }

    override fun sendCommand(type: Int, value: String, table: Int) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val command = json.encodeToString(Command(type, value, table))
//                webSocketManager.send(command)
//            } catch (e: Exception) {
//                Napier.e(tag = "WebSocket", message = "Failed to send command: ${e.message}")
//            }
//        }
    }

    fun connectToWebSocket(url: String) {
//        webSocketClient.reset()
//        webSocketClient.updateCallbacks(this)
//        webSocketClient.updateKtorWebsocketClient(url, AppStateProvider())
        connect()
    }

    private fun connect() {
        viewModelScope.launch(Dispatchers.IO) {
            //webSocketClient.connect()
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
            table?.let {
                _uiState.update { it.copy(currentTable = table) }
                Napier.e(tag = "SAVEDTABLE", message = "TABLE IN QUEUEVIEWMODEL: ${_uiState.value.currentTable}")
            }
        }
    }


    override fun updateSongs() {
        //sendCommandToServer(type = 26, value = "[\"playlist\"]", table = _uiState.value.currentTable)
//        viewModelScope.launch(coroutineExceptionHandler) {
//            val songs = queueRepository.getSongs()
//            _uiState.update { state -> state.copy(songs = songs) }
//        }
    }

    fun moveSongInQueue(oldIndex: Int, newIndex: Int) {
        if (oldIndex == newIndex) return  // Исключаем ненужные обновления

        val updatedSongs = _uiState.value.currentPlaylist.toMutableList()
        val song = updatedSongs.removeAt(oldIndex)
        updatedSongs.add(newIndex, song)

        _uiState.update { currentState ->
            currentState.copy(currentPlaylist = updatedSongs) // Обновляем только список
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch(Dispatchers.IO) {
            //webSocketClient.stop()
        }
    }

    override fun onReceive(data: String) {
        processWebSocketMessage(data)
    }

    override fun onConnected() {
        Napier.d(tag = "WebSocket", message = "Connected to WebSocket")
        updateServerConnectionStatus(true)
        //sendCommandToServer(type = 17, value = "", table = 0)
        //sendCommandToServer(type = 26, value = "[\"playlist\"]", table = _uiState.value.currentTable)
    }

    override fun onDisconnected(reason: String) {
        Napier.d(tag = "WebSocket", message = "Disconnected: $reason")
        updateServerConnectionStatus(false)
    }

    override fun updateError(error: String?) {
        _uiState.update { it.copy(error = error) }
    }

    override fun addSongToQueue(song: Song) {
        /* */
    }

    override fun updateCurrentSong(song: Song?) {
        _uiState.update { currentState ->
            currentState.copy(currentSong = song?.toSongInQueue())
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

    override fun processWebSocketMessage(jsonString: String) {
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
                val processed = States.getMessage(key).processQueue(value?.toString() ?: "", _uiState)
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
                        _uiState.value = _uiState.value.copy(
                            songs = it.toSongsInQueue(),
                            serverData = it.toServerData()
                        )
                    } ?: Napier.e(tag = "WebSocket", message = "Error: failed to parse full server state")
                } else {
                    Napier.w(tag = "WebSocket", message = "Received non-ResponseDto message, ignoring: $jsonString")
                }
            }

            // Если хоть что-то было обработано или пришли пустые данные, обновляем статус загрузки
            if (dataProcessed || jsonObject.isEmpty()) {
                Napier.d(tag= "QUEUETEST", message = _uiState.value.currentPlaylist.toString())
                queueRepository.setSongs(_uiState.value.currentPlaylist.toSong())

                updateIsLoading(isLoading = false)
            }
        } catch (e: Exception) {
            Napier.e(tag = "WebSocket", message = "Error parsing data: ${e.message}")
        }
    }

    fun removeSong(songInQueue: SongInQueue) {
        val updatedPlaylist = _uiState.value.currentPlaylist.toMutableList()
        updatedPlaylist.remove(songInQueue) // Удаляем песню из локального списка
        _uiState.update { state ->
            state.copy(currentPlaylist = updatedPlaylist)
        }
        queueRepository.deleteSong(songInQueue) // Удаляем песню из репозитория
    }

    fun updateCurrentSong(song: SongInQueue?) {
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

    override fun updateSoundInPause(soundInPause: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(soundInPause = soundInPause)
        }
    }

    private fun updateServerConnectionStatus(isServerConnected: Boolean) {
        _uiState.update { it.copy(isServerConnected = isServerConnected) }
    }
}