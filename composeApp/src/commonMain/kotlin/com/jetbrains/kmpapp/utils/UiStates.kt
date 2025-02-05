package com.jetbrains.kmpapp.utils

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.jetbrains.kmpapp.domain.models.ServerData
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.domain.models.SongInQueue

@Stable
data class MainUiState(
    val songs: SnapshotStateList<Song> = SnapshotStateList(),
    val currentPlaylist: MutableList<Song> = mutableListOf(),
    val serverData: ServerData = ServerData("", 0),
    val searchQuery: String = "",
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val tempo: Float = 0f,
    val pitch: Int = 0,
    val volume: Float = 0.5f,
    val autoFullScreen: Boolean = false,
    val adaptatingTempo: Boolean = false,
    val defaultVideoUse: Boolean = false,
    val singingAssessment: Boolean = false,
    val soundInPause: Boolean = false,
    val hasPlus: Boolean = false,
    val currentTable: Int = -1,
    val savedQrCode: String = "",
    val isServerConnected: Boolean = true,
    val isLoading: Boolean = true,
    val isTabLoading: Boolean = true,
    val currentSongs: List<Song> = emptyList(),
    val error: String? = null,


)

@Stable
data class QrScanUiState(
    val isLoading: Boolean = false,
    val detectedQR: String = "",
    val qrCodeProcessed: Boolean = false,
    val error: String? = null
)

@Stable
data class HomeUiState(
    val isLoading: Boolean = true,
    val savedQrCode: String = "",
    val error: String? = null
)

@Stable
data class QueueUiState(
    val songs: SnapshotStateList<SongInQueue> = SnapshotStateList(),
    val currentPlaylist: MutableList<SongInQueue> = mutableListOf(),
    val serverData: ServerData = ServerData("", 0),
    val currentSong: SongInQueue? = null,
    val isPlaying: Boolean = false,
    val tempo: Float = 0f,
    val pitch: Int = 0,
    val volume: Float = 0.5f,
    val autoFullScreen: Boolean = false,
    val adaptatingTempo: Boolean = false,
    val defaultVideoUse: Boolean = false,
    val singingAssessment: Boolean = false,
    val soundInPause: Boolean = false,
    val hasPlus: Boolean = false,
    val currentTable: Int = -1,
    val savedQrCode: String = "",
    val isServerConnected: Boolean = true,
    val isLoading: Boolean = true,
    val error: String? = null
)