package com.jetbrains.kmpapp.feature.sockets

import platform.UIKit.UIApplication
import platform.UIKit.UIBackgroundTaskIdentifier
import platform.UIKit.UIBackgroundTaskInvalid
//import platform.UIKit
//import platform.UIKit.beginBackgroundTask
//import platform.UIKit.endBackgroundTask
import com.jetbrains.kmpapp.data.sockets.WebSocketService

class WebSocketWorker {
    private val webSocketService = IOSWebSocketService() // Реализация WebSocketService для iOS
    private var backgroundTaskId: UIBackgroundTaskIdentifier = UIBackgroundTaskInvalid

    init {
        startBackgroundTask()
    }

    private fun startBackgroundTask() {
        backgroundTaskId = UIApplication().beginBackgroundTaskWithName("WebSocketTask") {
            // Если задача завершилась, сообщаем системе, что работа окончена
            if (backgroundTaskId != UIBackgroundTaskInvalid) {
                UIApplication().endBackgroundTask(backgroundTaskId)
                backgroundTaskId = UIBackgroundTaskInvalid
            }
        }

        println("WebSocketWorker: WebSocketService initialized in background.")
    }

    fun stopBackgroundTask() {
        if (backgroundTaskId != UIBackgroundTaskInvalid) {
            UIApplication().endBackgroundTask(backgroundTaskId)
            backgroundTaskId = UIBackgroundTaskInvalid
        }
    }

    fun getWebSocketService(): WebSocketService = webSocketService
}
