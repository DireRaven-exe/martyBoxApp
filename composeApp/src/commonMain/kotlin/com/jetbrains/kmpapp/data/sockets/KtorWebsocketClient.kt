package com.jetbrains.kmpapp.data.sockets

import com.jetbrains.kmpapp.di.AppStateProvider
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.time.Duration.Companion.milliseconds

class KtorWebsocketClient {
    private var _url: String = ""
    private var listener: WebsocketEvents? = null
    private var _appStateProvider: AppStateProvider? = null

    private val client = HttpClient {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(WebSockets) {
            pingInterval = 10_000.milliseconds
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5_000
            connectTimeoutMillis = 5_000
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO) + SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
        Napier.e(tag = TAG, message = "Error: ${throwable.message}")
    }

    private var job: Job? = null
    private var session: WebSocketSession? = null
    private var reconnectAttempts = 0
    private var isStopped = false

    suspend fun connect() {
        if (_url.isEmpty()) {
            Napier.e(tag = TAG, message = "WebSocket URL is empty, skipping connection.")
            return
        }

        if (isStopped) return

        try {
            Napier.d(tag = TAG, message = "Connecting to websocket at $_url...")

            session = client.webSocketSession(_url)

            reconnectAttempts = 0
            listener?.onConnected()

            Napier.i(tag = TAG, message = "Connected to websocket at $_url")

            scope.launch {
                while (!isStopped) {
                    if (session?.isActive == true) {
                        sendPing()
                    } else {
                        connect()
                    }
                    delay(PING_INTERVAL)
                }
            }

            session!!.incoming
                .receiveAsFlow()
                .filterIsInstance<Frame.Text>()
                .filterNotNull()
                .collect { data ->
                    val message = data.readText()
                    listener?.onReceive(message)

                    Napier.i(tag = TAG, message = "Received message: success")
                }
        } catch (e: Exception) {
            Napier.e(tag = TAG, message = "Error: ${e.message}")
            if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                reconnect()
            } else {
                Napier.d(tag = TAG, message = "Max reconnect attempts exceeded. Reconnection stopped.")
                listener?.onDisconnected("Max reconnect attempts exceeded.")
            }
        }
    }

    private fun reconnect() {
        if (isStopped) {
            Napier.d(tag = TAG, message = "Reconnection aborted: client is stopped.")
            return
        }

        job?.cancel()

        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            Napier.e(tag = TAG, message = "Max reconnect attempts exceeded. Server marked as disconnected.")
            listener?.onDisconnected("Max reconnect attempts exceeded.")
            scope.launch { stop() }
            return
        }

        val delayTime = (RECONNECT_DELAY * (reconnectAttempts + 1)).coerceAtMost(5_000L)

        Napier.d(tag = TAG, message = "Reconnecting to WebSocket in $delayTime ms...")

        job = scope.launch {
            delay(delayTime)
            if (isStopped) return@launch

            reconnectAttempts++
            try {
                connect()
            } catch (e: Exception) {
                // Catch any other connection issues (e.g., network, server down)
                Napier.e(tag = TAG, message = "Reconnection failed: ${e.message}")
                listener?.onDisconnected("Reconnection failed: ${e.message}")
            }
        }
    }

    suspend fun stop() {
        Napier.d(tag = TAG, message = "Stopping WebSocket client...")

        isStopped = true
        job?.cancel()
        scope.coroutineContext.cancelChildren()

        session?.close()
        session = null

        Napier.d(tag = TAG, message = "WebSocket client stopped.")
        listener?.onDisconnected("Connection lost")
    }

    suspend fun send(message: String) {
        if (isStopped) return

        Napier.d(tag = TAG, message = "Sending message: $message")

        try {
            session?.send(Frame.Text(message))
            Napier.d(tag = TAG, message = "$message sent")
        } catch (e: Exception) {
            Napier.e(tag = TAG, message = "Error sending ping: ${e.message}")
            stop()
        }
    }

    fun reset() {
        isStopped = false
        reconnectAttempts = 0
        session = null
    }

    private suspend fun sendPing() {
        if (isStopped) return

        try {
            session?.send(Frame.Text("ping"))
            Napier.d(tag = TAG, message = "Ping sent")
            listener?.onPingMessage()
        } catch (e: Exception) {
            Napier.e(tag = TAG, message = "Error sending ping: ${e.message}")
            reconnect()
        }
    }

    fun updateKtorWebsocketClient(url: String, appStateProvider: AppStateProvider) {
        _url = url
        _appStateProvider = appStateProvider
    }

    fun updateCallbacks(listener: WebsocketEvents) {
        this.listener = listener
    }

    interface WebsocketEvents {
        fun onReceive(data: String)
        fun onConnected()
        fun onDisconnected(reason: String)

        fun onPingMessage()
    }

    companion object {
        private const val RECONNECT_DELAY = 3_000L
        private const val PING_INTERVAL = 10_000L
        private const val MAX_RECONNECT_ATTEMPTS = 2
        private const val TAG = "EVChargingWebSocketClient"
    }
}
