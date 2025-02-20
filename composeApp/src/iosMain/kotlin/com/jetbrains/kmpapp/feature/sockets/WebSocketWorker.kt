package com.jetbrains.kmpapp.feature.sockets

import com.jetbrains.kmpapp.data.sockets.WebSocketService
import platform.UIKit.UIApplication
import platform.UIKit.UIBackgroundTaskIdentifier
import platform.UIKit.beginBackgroundTask
import platform.UIKit.endBackgroundTask

class WebSocketWorker {
    private val webSocketService = IOSWebSocketService() // Твой сервис, как AndroidWebSocketService
    private var backgroundTaskId: UIBackgroundTaskIdentifier = UIBackgroundTaskIdentifier.invalid

    init {
        startBackgroundTask()
    }

    private fun startBackgroundTask() {
        backgroundTaskId = UIApplication.shared.beginBackgroundTask {
            // Если задача завершилась, сообщаем системе, что работа окончена
            UIApplication.shared.endBackgroundTask(backgroundTaskId)
            backgroundTaskId = UIBackgroundTaskIdentifier.invalid
        }

        // Инициализация WebSocket-сервиса
        println("WebSocketWorker: WebSocketService initialized in background.")
    }

    fun stopBackgroundTask() {
        if (backgroundTaskId != UIBackgroundTaskIdentifier.invalid) {
            UIApplication.shared.endBackgroundTask(backgroundTaskId)
            backgroundTaskId = UIBackgroundTaskIdentifier.invalid
        }
    }

    fun getWebSocketService(): WebSocketService = webSocketService
}
