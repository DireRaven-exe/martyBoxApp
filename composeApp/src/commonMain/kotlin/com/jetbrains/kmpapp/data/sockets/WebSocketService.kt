package com.jetbrains.kmpapp.data.sockets

import kotlinx.coroutines.flow.Flow

interface WebSocketService {
    fun connect(url: String)
    fun disconnect()
    fun sendMessage(message: String)
    fun observeMessages(): Flow<String>
    fun observeConnectionState(): Flow<WebSocketConnectionState>
}

sealed class WebSocketConnectionState {
    object Connected : WebSocketConnectionState()
    object Disconnected : WebSocketConnectionState()
    data class Error(val error: String) : WebSocketConnectionState()
}
