import SwiftUI
import UIKit
import ComposeApp
import Combine

class WebSocketWorkerWrapper: ObservableObject {
    private let webSocketWorker: WebSocketWorker

    init() {
        self.webSocketWorker = WebSocketWorker()
    }

    func getWebSocketService() -> WebSocketService {
        return webSocketWorker.getWebSocketService()
    }

    func startBackgroundTask() {
        webSocketWorker.startBackgroundTask()
    }

    func stopBackgroundTask() {
        webSocketWorker.stopBackgroundTask()
    }
}

