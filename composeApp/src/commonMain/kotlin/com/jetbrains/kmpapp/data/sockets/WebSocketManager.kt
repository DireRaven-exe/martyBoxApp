package com.jetbrains.kmpapp.data.sockets

import com.jetbrains.kmpapp.di.AppStateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import kotlin.concurrent.Volatile
import kotlin.jvm.JvmStatic

class WebSocketManager : KtorWebsocketClient.WebsocketEvents {

    private val webSocketClient = KtorWebsocketClient()
    private val listeners = mutableListOf<KtorWebsocketClient.WebsocketEvents>()

    private var _isConnected = MutableStateFlow(false) // состояние соединения
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    fun connect(url: String) {

        CoroutineScope(Dispatchers.Main).launch {
            webSocketClient.updateKtorWebsocketClient(url, AppStateProvider())
            webSocketClient.updateCallbacks(this@WebSocketManager)
            webSocketClient.reset()

            webSocketClient.connect()
        }
    }

    fun disconnect() {
        CoroutineScope(Dispatchers.Main).launch {
            webSocketClient.stop()
        }
    }

    fun send(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            webSocketClient.send(message)
        }
    }

    fun addListener(listener: KtorWebsocketClient.WebsocketEvents) {
        listeners.add(listener)
    }

    fun removeListener(listener: KtorWebsocketClient.WebsocketEvents) {
        listeners.remove(listener)
    }

    override fun onReceive(data: String) {
        listeners.forEach { it.onReceive(data) }
    }

    override fun onConnected() {
        _isConnected.value = true
        listeners.forEach { it.onConnected() }
    }

    override fun onDisconnected(reason: String) {
        _isConnected.value = false
        listeners.forEach { it.onDisconnected(reason) }
    }

    override fun onPingMessage() {
        listeners.forEach { it.onPingMessage() }
    }

    companion object {
        @Volatile
        private var instance: WebSocketManager? = null

        // Объект для синхронизации
        @OptIn(InternalCoroutinesApi::class)
        private val lock = SynchronizedObject()

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(): WebSocketManager {
            return instance ?: synchronized(lock) {
                instance ?: WebSocketManager().also { instance = it }
            }
        }

        @JvmStatic
        val shared: WebSocketManager
            get() = getInstance()
    }



    fun isConnected() : Boolean {
        return _isConnected.value
    }
}
