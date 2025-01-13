package com.jetbrains.kmpapp.data.sockets

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

class KtorWebsocketClient(
    private val url: String,
    private val listener: WebsocketEvents,
) {
    private val client = HttpClient {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(WebSockets) {
            pingInterval = 5_000.milliseconds
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
        if (isStopped) return

        try {
            Napier.d(tag = TAG, message = "Connecting to websocket at $url...")

            session = client.webSocketSession(url)

            reconnectAttempts = 0
            listener.onConnected()

            Napier.i(tag = TAG, message = "Connected to websocket at $url")

            scope.launch {
                while (session?.isActive == true && !isStopped) {
                    sendPing()
                    delay(PING_INTERVAL)
                }
            }

            session!!.incoming
                .receiveAsFlow()
                .filterIsInstance<Frame.Text>()
                .filterNotNull()
                .collect { data ->
                    val message = data.readText()
                    listener.onReceive(message)

                    Napier.i(tag = TAG, message = "Received message: $message")
                }
        } catch (e: Exception) {
            Napier.e(tag = TAG, message = "Error: ${e.message}")
            if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                reconnect()
            } else {
                Napier.d(tag = TAG, message = "Max reconnect attempts exceeded. Reconnection stopped.")
                listener.onDisconnected("Max reconnect attempts exceeded.")
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
            listener.onDisconnected("Max reconnect attempts exceeded.")
            return
        }

        Napier.d(tag = TAG, message = "Reconnecting to WebSocket in ${RECONNECT_DELAY}ms...")

        job = scope.launch {
            delay(RECONNECT_DELAY)
            if (isStopped) return@launch

            reconnectAttempts++
            connect()
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
    }

    suspend fun send(message: String) {
        if (isStopped) return

        Napier.d(tag = TAG, message = "Sending message: $message")

        session?.send(Frame.Text(message))
    }

    private suspend fun sendPing() {
        if (isStopped) return

        try {
            session?.send(Frame.Text("ping"))
            Napier.d(tag = TAG, message = "Ping sent")
        } catch (e: Exception) {
            Napier.e(tag = TAG, message = "Error sending ping: ${e.message}")
            reconnect()
        }
    }

    interface WebsocketEvents {
        fun onReceive(data: String)
        fun onConnected()
        fun onDisconnected(reason: String)
    }

    companion object {
        private const val RECONNECT_DELAY = 3_000L
        private const val PING_INTERVAL = 5_000L
        private const val MAX_RECONNECT_ATTEMPTS = 2
        private const val TAG = "EVChargingWebSocketClient"
    }
}
