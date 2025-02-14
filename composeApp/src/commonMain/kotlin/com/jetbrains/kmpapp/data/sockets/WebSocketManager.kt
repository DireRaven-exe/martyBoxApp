package com.jetbrains.kmpapp.data.sockets

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import kotlin.concurrent.Volatile

class WebSocketManager : KtorWebsocketClient.WebsocketEvents {

    private val webSocketClient = KtorWebsocketClient()
    private var listener: KtorWebsocketClient.WebsocketEvents? = null

    private var _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var connectJob: Job? = null

    fun connect(url: String) {
        connectJob?.cancel() // Отменяем предыдущее подключение
        connectJob = scope.launch {
            webSocketClient.updateKtorWebsocketClient(url)
            webSocketClient.updateCallbacks(this@WebSocketManager)
            webSocketClient.reset()
            webSocketClient.connect()
        }
    }

    fun disconnect() {
        Napier.d(tag = "WebSocket", message = "ABOBA")
        connectJob?.cancel() // Отменяем текущее подключение
        scope.launch {
            webSocketClient.stop()
        }
    }

    fun send(message: String) {
        Napier.e(tag = "SenderMessages", message = "$this")
        scope.launch(Dispatchers.IO) {
            webSocketClient.send(message)
        }
    }

    fun addListener(listener: KtorWebsocketClient.WebsocketEvents) {
        this.listener = listener
    }

    fun removeListener() {
        this.listener = null
    }

    override fun onReceive(data: String) {
        listener?.onReceive(data)
    }

    override fun onConnected() {
        _isConnected.value = true
        listener?.onConnected()
    }

    override fun onDisconnected(reason: String) {
        _isConnected.value = false
        listener?.onDisconnected(reason)
    }

    override fun onPingMessage() {
        listener?.onPingMessage()
    }

    // Полное уничтожение WebSocketManager
    fun destroy() {
        disconnect() // Останавливаем WebSocket
        scope.cancel() // Отменяем все корутины
        instance = null // Удаляем singleton
    }

    companion object {
        @Volatile
        private var instance: WebSocketManager? = null

        @OptIn(InternalCoroutinesApi::class)
        private val lock = SynchronizedObject()

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(): WebSocketManager {
            return instance ?: synchronized(lock) {
                instance ?: WebSocketManager().also { instance = it }
            }
        }
    }
}