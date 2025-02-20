import SwiftUI
import UIKit
import ComposeApp

class WebSocketWorker: ObservableObject {
    private val webSocketService = IOSWebSocketService() // Твой WebSocketService
    private var backgroundTaskId: UIBackgroundTaskIdentifier = UIBackgroundTaskIdentifier.invalid

    init() {
        startBackgroundTask()
    }

    private func startBackgroundTask() {
        backgroundTaskId = UIApplication.shared.beginBackgroundTask {
            // Завершаем задачу, если система это требует
            UIApplication.shared.endBackgroundTask(self.backgroundTaskId)
            self.backgroundTaskId = UIBackgroundTaskIdentifier.invalid
        }

        // Логика для инициализации WebSocketService
        print("WebSocketWorker: WebSocketService initialized.")
    }

    func stopBackgroundTask() {
        if backgroundTaskId != UIBackgroundTaskIdentifier.invalid {
            UIApplication.shared.endBackgroundTask(backgroundTaskId)
            backgroundTaskId = UIBackgroundTaskIdentifier.invalid
        }
    }

    deinit {
        stopBackgroundTask()
    }

    // Добавим методы для взаимодействия с WebSocketService, если потребуется
    func getWebSocketService() -> WebSocketService {
        return webSocketService
    }
}
