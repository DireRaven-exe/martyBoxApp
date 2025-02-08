//import ComposeApp
//import Foundation
//import BackgroundTasks
//
//class WebSocketBackgroundTask {
//
//    static let identifier = "com.martin.websocket.ping"
//
//    func schedule() {
//        let request = BGProcessingTaskRequest(identifier: WebSocketBackgroundTask.identifier)
//        request.requiresNetworkConnectivity = true
//        request.requiresExternalPower = false
//
//        do {
//            try BGTaskScheduler.shared.submit(request)
//        } catch {
//            print("Could not schedule WebSocket ping task: \(error)")
//        }
//    }
//
//    func handle(task: BGTask) {
//        schedule()
//
//        // Используем WebSocketManager.shared из Kotlin
//        let webSocketManager = WebSocketManager.companion.shared
//        webSocketManager.send(message: "ping")
//
//        task.setTaskCompleted(success: true)
//    }
//}

//import Combine
//import Foundation
//import ComposeApp
//
//class WebSocketService {
//    private var webSocketManager: WebSocketManager?
//    private var cancellables = Set<AnyCancellable>()
//
//    func start() {
//        // Получаем экземпляр WebSocketManager
//        webSocketManager = WebSocketManager.companion.shared
//
//        // Начинаем наблюдать за qrCode
//        observeQrCodeAndConnect()
//    }
//
//    private func observeQrCodeAndConnect() {
//        let appPreferencesRepository = AppPreferencesRepository()
//
//        // Подписываемся на изменения qrCode
//        appPreferencesRepository.getQrCode()
//            .compactMap { $0 } // Пропускаем nil значения
//            .sink { [weak self] qrCode in
//                // Подключаемся к WebSocket с новым qrCode
//                self?.connectToWebSocket(qrCode: qrCode)
//            }
//            .store(in: &cancellables)
//    }
//
//    private func connectToWebSocket(qrCode: String) {
//        // Отключаем старое соединение
//        webSocketManager?.disconnect()
//
//        // Ждем 1 секунду перед подключением к новому WebSocket
//        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
//            self.webSocketManager?.connect(url: qrCode)
//        }
//    }
//
//    func stop() {
//        // Останавливаем соединение с WebSocket
//        webSocketManager?.disconnect()
//
//        // Отменяем все подписки
//        cancellables.removeAll()
//    }
//}
