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
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.io.IOException
import kotlin.concurrent.Volatile
import kotlin.time.Duration.Companion.milliseconds

class KtorWebsocketClient {
    private var _url: String = ""
    private var listener: WebsocketEvents? = null

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
    @Volatile private var isConnected = false // Флаг подключения
    private var isConnecting = false // Флаг состояния подключения

    suspend fun connect() {
        reset()
        if (_url.isEmpty()) {
            Napier.e(tag = TAG, message = "WebSocket URL is empty, skipping connection.")
            return
        }

        if (isStopped || isConnected || isConnecting) {
            // Если уже подключен, подключение выполняется, или соединение остановлено, выходим
            Napier.i(tag = TAG, message = "Skipping connect: isStopped=$isStopped, isConnected=$isConnected, isConnecting=$isConnecting")
            return
        }

        isConnecting = true // Помечаем, что началось подключение

        try {
            session = client.webSocketSession(_url)
            isConnected = true
            reconnectAttempts = 0
            listener?.onConnected()

            Napier.i(tag = TAG, message = "Connected to websocket at $_url")

            // Стартуем задачу для пинга
            scope.launch {
                while (!isStopped) {
                    if (session?.isActive == true) {
                        sendPing()
                    } else {
                        Napier.e(tag = TAG, message = "Session not active, reconnecting...")
                        reconnect()
                    }
                    delay(PING_INTERVAL)
                }
            }

            // Читаем входящие сообщения
            session!!.incoming
                .receiveAsFlow()
                .filterIsInstance<Frame.Text>()
                .catch { e ->
                    Napier.e(tag = TAG, message = "Error receiving message: ${e.message}")
                    when (e) {
                        is IOException, is ClosedReceiveChannelException -> {
                            Napier.e(tag = TAG, message = "Connection reset detected, reconnecting...")
                            reconnect()
                        }
                        else -> throw e
                    }
                }
                .collect { data ->
                    val message = data.readText()
                    listener?.onReceive(message)
                    Napier.i(tag = TAG, message = "Received message: success")
                }
        } catch (e: Exception) {
            Napier.e(tag = TAG, message = "Error during connection: ${e.message}")
            isConnected = false
            isConnecting = false

            if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS && !isStopped) {
                reconnect()
            } else {
                Napier.d(tag = TAG, message = "Max reconnect attempts exceeded. Reconnection stopped.")
                listener?.onDisconnected("Max reconnect attempts exceeded.")
            }
        } finally {
            isConnecting = false // Сбрасываем флаг подключения
        }
    }


    private fun reconnect() {
        if (isStopped || isConnected || reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            Napier.e(tag = TAG, message = "Max reconnect attempts exceeded. Stopping client.")
            Napier.d(tag = "WebSocket", message = "ABOBA")
            scope.launch { stop() }
            return
        }

        //reset()

        val delayTime = (RECONNECT_DELAY * (reconnectAttempts + 1)).coerceAtMost(5_000L)

        Napier.d(tag = TAG, message = "Reconnecting in $delayTime ms... (Attempt ${reconnectAttempts + 1})")

        job?.cancel()
        job = scope.launch {
            delay(delayTime)
            if (isStopped) return@launch

            reconnectAttempts++
            try {
                connect()
            } catch (e: Exception) {
                Napier.e(tag = TAG, message = "Reconnection failed: ${e.message}")
            }
        }
    }

    suspend fun stop() {
        Napier.d(tag = TAG, message = "Stopping WebSocket client...")

        isStopped = true
        isConnecting = false
        isConnected = false
        job?.cancel()
        scope.coroutineContext.cancelChildren()

        try {
            session?.close()
        } catch (e: Exception) {
            Napier.e(tag = TAG, message = "Error closing session: ${e.message}")
        }
        session = null

        try {
            client.close()
        } catch (e: Exception) {
            Napier.e(tag = TAG, message = "Error closing client: ${e.message}")
        }

        Napier.d(tag = TAG, message = "WebSocket client stopped.")
        listener?.onDisconnected("Connection lost")
    }

    suspend fun send(message: String) {
        if (isStopped || session?.isActive != true) return

        Napier.d(tag = TAG, message = "Sending message: $message")

        try {
            session?.send(Frame.Text(message))
            Napier.d(tag = TAG, message = "$message sent")
        } catch (e: Exception) {
            Napier.e(tag = TAG, message = "Error sending message: ${e.message}")
            reconnect()
        }
    }



    private suspend fun sendPing() {
        if (isStopped || session?.isActive != true) return

        try {
            session?.send(Frame.Text("ping"))
            Napier.d(tag = TAG, message = "Ping sent")
            listener?.onPingMessage()
        } catch (e: Exception) {
            Napier.e(tag = TAG, message = "Error sending ping: ${e.message}")
            reconnect()
        }
    }

    fun reset() {
        isStopped = false
        isConnected = false
        isConnecting = false
        reconnectAttempts = 0
        session = null
    }
    fun updateKtorWebsocketClient(url: String) {
        _url = url
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
        private const val MAX_RECONNECT_ATTEMPTS = 3
        private const val TAG = "EVChargingWebSocketClient"
    }
}
