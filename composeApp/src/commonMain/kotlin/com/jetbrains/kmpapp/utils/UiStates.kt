package com.jetbrains.kmpapp.utils

import com.jetbrains.kmpapp.domain.models.ServerData
import com.jetbrains.kmpapp.domain.models.Song

data class MainUiState(
    val songs: List<Song> = emptyList(),
    val serverData: ServerData = ServerData("", 0),
    var searchQuery: String = "",
    var searchBarActive: Boolean = false,
    var currentSong: Song? = null,
    var isPlaying: Boolean = false,
    var tempo: Float = 0f,
    var pitch: Int = 0,
    var volume: Float = 0.5f,
    var autoFullScreen: Boolean = false,
    var adaptatingTempo: Boolean = false,
    var defaultVideoUse: Boolean = false,
    var singingAssessment: Boolean = false,
    var soundInPause: Boolean = false,
    var hasPlus: Boolean = false,
    var currentTable: Int = -1,
    var savedQrCode: String = "",
    var isServerConnected: Boolean = true,
    var isLoading: Boolean = true,
    val error: String? = null
)

data class QrScanUiState(
    val isLoading: Boolean = false,
    val detectedQR: String = "",
    var qrCodeProcessed: Boolean = false,
    val error: String? = null
)

data class HomeUiState(
    var isLoading: Boolean = true,
    var savedQrCode: String = "",
    val error: String? = null
)
