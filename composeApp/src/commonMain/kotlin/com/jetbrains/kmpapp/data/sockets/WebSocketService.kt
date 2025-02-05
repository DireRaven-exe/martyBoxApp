package com.jetbrains.kmpapp.data.sockets

import com.jetbrains.kmpapp.di.AppStateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import kotlin.concurrent.Volatile

class WebSocketManager private constructor() : KtorWebsocketClient.WebsocketEvents {

    private val webSocketClient = KtorWebsocketClient()
    private val listeners = mutableListOf<KtorWebsocketClient.WebsocketEvents>()

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
        listeners.forEach { it.onConnected() }
    }

    override fun onDisconnected(reason: String) {
        listeners.forEach { it.onDisconnected(reason) }
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
    }
}