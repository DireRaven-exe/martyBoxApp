package com.jetbrains.kmpapp.feature.sockets

import com.jetbrains.kmpapp.data.sockets.KtorWebsocketClient
import com.jetbrains.kmpapp.data.sockets.WebSocketConnectionState
import com.jetbrains.kmpapp.data.sockets.WebSocketService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AndroidWebSocketService : WebSocketService {
    private val messageFlow = MutableSharedFlow<String>()
    private val connectionStateFlow = MutableStateFlow<WebSocketConnectionState>(WebSocketConnectionState.Disconnected)
    private val ktorClient = KtorWebsocketClient()
    private var scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        ktorClient.updateCallbacks(object : KtorWebsocketClient.WebsocketEvents {
            override fun onReceive(data: String) {
                Napier.d(tag = "AndroidWebSocket", message = "onReceive function")
                scope.launch {
                    messageFlow.emit(data)
                }
            }

            override fun onConnected() {
                Napier.d(tag = "AndroidWebSocket", message = "onConnected function = $this")
                if (connectionStateFlow.value != WebSocketConnectionState.Connected) {
                    scope.launch {
                        connectionStateFlow.emit(WebSocketConnectionState.Connected)
                    }
                }
            }

            override fun onDisconnected(reason: String) {
                Napier.d(tag = "AndroidWebSocket", message = "onDisconnected function")
                scope.launch {
                    connectionStateFlow.emit(WebSocketConnectionState.Disconnected)
                }
            }

            override fun onPingMessage() {
                scope.launch {
                    // Логика обработки ping (например, логирование или отправка pong-сообщения)
                    println("Ping received from server")
                }
            }
        })
    }

    override fun connect(url: String) {
        scope.launch {
            //connectionStateFlow.emit(WebSocketConnectionState.Disconnected) // Сбрасываем состояние перед подключением
            try {
                ktorClient.updateKtorWebsocketClient(url)
                ktorClient.connect()
            } catch (e: Exception) {
                connectionStateFlow.emit(WebSocketConnectionState.Error("Connection failed: ${e.message}"))
            }
        }
    }

    override fun disconnect() {
        scope.launch {
            try {
                ktorClient.stop()
                connectionStateFlow.emit(WebSocketConnectionState.Disconnected)
            } catch (e: Exception) {
                connectionStateFlow.emit(WebSocketConnectionState.Error("Disconnection failed: ${e.message}"))
            } finally {
                scope.cancel() // Завершение scope для освобождения ресурсов
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()) // Новый scope для повторного подключения
            }
        }
    }

    override fun sendMessage(message: String) {
        scope.launch {
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
