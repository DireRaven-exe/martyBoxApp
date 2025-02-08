//import SwiftUI
//import ComposeApp
//import BackgroundTasks
//
//
//@main
//struct iOSApp: App {
//    init() {
//        KoinKt.doInitKoin()
//
//        BGTaskScheduler.shared.register(forTaskWithIdentifier: WebSocketBackgroundTask.identifier, using: nil) { task in
//            WebSocketBackgroundTask().handle(task: task)
//        }
//    }
//
//    var body: some Scene {
//        WindowGroup {
//            ContentView()
//            .onAppear {
//                WebSocketBackgroundTask().schedule()
//            }
//        }
//    }
//}

import ComposeApp
import SwiftUI
import BackgroundTasks

@main
struct iOSApp: App {
    // Экземпляр WebSocketService
    @StateObject private var webSocketService = WebSocketService()

    init() {
        // Регистрация фоновой задачи для WebSocket
        BGTaskScheduler.shared.register(forTaskWithIdentifier: WebSocketBackgroundTask.identifier, using: nil) { task in
            WebSocketBackgroundTask().handle(task: task)
        }

        // Инициализация Koin и прочих зависимостей
        KoinKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onAppear {
                    // Запуск WebSocketService, когда приложение появляется
                    webSocketService.start()
                }
                .onDisappear {
                    // Остановка службы, когда приложение исчезает
                    webSocketService.stop()
                }
        }
    }
}