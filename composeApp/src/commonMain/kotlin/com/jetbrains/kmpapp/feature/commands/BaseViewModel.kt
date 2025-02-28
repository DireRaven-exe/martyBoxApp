package com.jetbrains.kmpapp.feature.commands

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.data.sockets.KtorWebsocketClient
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.domain.models.SongInQueue
import com.jetbrains.kmpapp.feature.datastore.AppPreferencesRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

abstract class BaseViewModel(
    protected val appPreferencesRepository: AppPreferencesRepository,
    //private val webSocketService: KtorWebsocketClient
) : ViewModel(), KtorWebsocketClient.WebsocketEvents {

    protected val json = Json { ignoreUnknownKeys = true }
    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        updateError(exception.message)
    }

    abstract fun processWebSocketMessage(jsonString: String)
    abstract fun updateError(error: String?)
    abstract fun addSongToQueue(song: Song)
    abstract fun updateCurrentSong(song: Song?)
    abstract fun updateVolume(volume: Float)
    abstract fun updateSoundInPause(soundInPause: Boolean)
    abstract fun updateTempo(tempo: Float)
    abstract fun updatePitch(pitch: Int)
    abstract fun updateAutoFullScreen(autoFullScreen: Boolean)
    abstract fun updateSingingAssessment(singingAssessment: Boolean)

    abstract fun updateSongsForTab(tabName: String)

    abstract fun updateQueue()

    abstract fun moveSongInQueue(oldIndex: Int, newIndex: Int)

    abstract fun sendCommand(type: Int, value: String, table: Int)

    abstract fun removeSong(songInQueue: SongInQueue)

    abstract fun clearQueue()

    fun clearSavedQrCode() {
        viewModelScope.launch(coroutineExceptionHandler) {
            appPreferencesRepository.clearQrCode()
        }
    }

    fun clearSavedTableNumber() =
        viewModelScope.launch(coroutineExceptionHandler) {
            appPreferencesRepository.saveTableNumber(tableNumber = -1)
        }



    fun saveCurrentTable(currentTable: Int) =
        viewModelScope.launch(coroutineExceptionHandler) {
            appPreferencesRepository.saveTableNumber(tableNumber = currentTable)
        }
}