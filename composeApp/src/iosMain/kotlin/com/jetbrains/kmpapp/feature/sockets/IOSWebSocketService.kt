package com.jetbrains.kmpapp.feature.sockets

import com.jetbrains.kmpapp.data.sockets.KtorWebsocketClient
import com.jetbrains.kmpapp.data.sockets.WebSocketConnectionState
import com.jetbrains.kmpapp.data.sockets.WebSocketService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

object WebSocketServiceHolder {
    val webSocketService: WebSocketService = IOSWebSocketService()
}


class IOSWebSocketService : WebSocketService {
    private val messageFlow = MutableSharedFlow<String>()
    private val connectionStateFlow = MutableStateFlow<WebSocketConnectionState>(WebSocketConnectionState.Disconnected)
    private val ktorClient = KtorWebsocketClient()
    private var coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        ktorClient.updateCallbacks(object : KtorWebsocketClient.WebsocketEvents {
            override fun onReceive(data: String) {
                coroutineScope.launch {
                    messageFlow.emit(data)
                }
            }

            override fun onConnected() {
                coroutineScope.launch {
                    connectionStateFlow.emit(WebSocketConnectionState.Connected)
                }
            }

            override fun onDisconnected(reason: String) {
                coroutineScope.launch {
                    connectionStateFlow.emit(WebSocketConnectionState.Disconnected)
                }
            }

            override fun onPingMessage() {
                coroutineScope.launch {
                    println("Ping received from server")
                }
            }
        })
    }

    override fun connect(url: String) {
        coroutineScope.launch {
            try {
                ktorClient.updateKtorWebsocketClient(url)
                ktorClient.connect()
                connectionStateFlow.emit(WebSocketConnectionState.Connected)
            } catch (e: Exception) {
                connectionStateFlow.emit(WebSocketConnectionState.Error("Connection failed: ${e.message}"))
            }
        }
    }

    override fun disconnect() {
        coroutineScope.launch {
            try {
                ktorClient.stop()
                connectionStateFlow.emit(WebSocketConnectionState.Disconnected)
            } catch (e: Exception) {
                connectionStateFlow.emit(WebSocketConnectionState.Error("Disconnection failed: ${e.message}"))
            }
        }
    }

    override fun sendMessage(message: String) {
        coroutineScope.launch {
            try {
                ktorClient.send(message)
            } catch (e: Exception) {
                connectionStateFlow.emit(WebSocketConnectionState.Error("Failed to send message: ${e.message}"))
            }
        }
    }

    override fun observeMessages(): Flow<String> = messageFlow

    override fun observeConnectionState(): Flow<WebSocketConnectionState> = connectionStateFlow
}
