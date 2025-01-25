package com.jetbrains.kmpapp.ui.screens.queue

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.feature.datastore.QueueRepository
import com.jetbrains.kmpapp.utils.QueueUiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QueueViewModel(
    private val queueRepository: QueueRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(QueueUiState())
    val uiState = _uiState.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        _uiState.update { it.copy(isLoading = false, error = exception.message) }
    }

    init {
        updateSongs()
    }

    private fun updateSongs() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { state -> state.copy(songs = queueRepository.getSongs()) }
        }
    }

    fun updateSongList(newSongList: SnapshotStateList<Song>) {
        queueRepository.setSongs(newSongList)
        _uiState.value = _uiState.value.copy(songs = newSongList)
    }

    // Новый метод для перемещения песни
    fun moveSongInQueue(oldIndex: Int, newIndex: Int) {
        queueRepository.editSongPosition(newIndex, oldIndex)
        updateSongs() // После изменения вызываем обновление списка
    }
}