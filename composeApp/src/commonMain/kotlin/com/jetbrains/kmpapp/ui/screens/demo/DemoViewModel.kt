package com.jetbrains.kmpapp.ui.screens.demo

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.domain.models.SongInQueue
import com.jetbrains.kmpapp.domain.models.toSong
import com.jetbrains.kmpapp.domain.models.toSongInQueue
import com.jetbrains.kmpapp.feature.commands.BaseViewModel
import com.jetbrains.kmpapp.feature.datastore.AppPreferencesRepository
import com.jetbrains.kmpapp.utils.DemoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
class DemoViewModel(
    appPreferencesRepository: AppPreferencesRepository,
) : BaseViewModel(appPreferencesRepository) {

    private val _uiState = MutableStateFlow(DemoUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            songs = generateDemoSongs().toMutableStateList(),
            currentPlaylist = generateDemoPlaylist().toMutableList(),
            currentSong = null,
            isLoading = false
        )
    }
    override fun onCleared() {
        super.onCleared()
        _uiState.value = DemoUiState()
    }

    fun playSoundInPlaylist(id: Int, song: SongInQueue) {
        updateCurrentSong(song.toSong())
        updateQueue()
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

    fun generateDemoSongs(): List<Song> {
        return listOf(
            Song(artist = "Queen", tab = "Rock", id = 1, title = "Bohemian Rhapsody"),
            Song(artist = "AC/DC", tab = "Rock", id = 2, title = "Back in Black"),
            Song(artist = "The Beatles", tab = "Pop", id = 3, title = "Hey Jude"),
            Song(artist = "Elvis Presley", tab = "Rock", id = 4, title = "Hound Dog"),
            Song(artist = "Taylor Swift", tab = "Pop", id = 5, title = "Shake It Off")
        )
    }

    fun generateDemoPlaylist(): List<SongInQueue> {
        return generateDemoSongs().map {
            SongInQueue(
                artist = it.artist,
                tab = it.tab,
                id = it.id,
                title = it.title
            )
        }
    }

    fun next() {
        val playlist = _uiState.value.currentPlaylist
        if (playlist.isNotEmpty()) {
            val currentIndex = playlist.indexOfFirst { it.id == _uiState.value.currentSong?.id }
            val nextIndex = (currentIndex + 1) % playlist.size // Циклический переход
            val nextSong = playlist[nextIndex]
            updateCurrentSong(nextSong.toSong())
        }
    }

    override fun removeSong(songInQueue: SongInQueue) {
        val updatedPlaylist = _uiState.value.currentPlaylist.toMutableList()
        updatedPlaylist.remove(songInQueue)
        _uiState.update { state ->
            state.copy(currentPlaylist = updatedPlaylist)
        }
    }

    override fun sendCommand(type: Int, value: String, table: Int) {}

    override fun onReceive(data: String) {}

    override fun onConnected() {}

    override fun onDisconnected(reason: String) { }

    override fun onPingMessage() {}

    override fun processWebSocketMessage(jsonString: String) {}

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

    override fun updateQueue() {}

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

    fun play(song: Song) {
        viewModelScope.launch(coroutineExceptionHandler)  {
            _uiState.update { currentState ->
                currentState.copy(currentSong = song)
            }
        }
    }
}