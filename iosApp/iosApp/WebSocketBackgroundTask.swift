import ComposeApp
import Foundation
import BackgroundTasks

class WebSocketBackgroundTask {

    static let identifier = "com.martin.websocket.ping"

    func schedule() {
        let request = BGProcessingTaskRequest(identifier: WebSocketBackgroundTask.identifier)
        request.requiresNetworkConnectivity = true
        request.requiresExternalPower = false

        do {
            try BGTaskScheduler.shared.submit(request)
        } catch {
            print("Could not schedule WebSocket ping task: \(error)")
        }
    }

    func handle(task: BGTask) {
        schedule()

        // Используем WebSocketManager.shared из Kotlin
        let webSocketManager = WebSocketManager.shared
        webSocketManager.send(message: "ping")

        task.setTaskCompleted(success: true)
    }
}

