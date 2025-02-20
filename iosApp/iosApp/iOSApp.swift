import ComposeApp
import SwiftUI
import BackgroundTasks

@main
struct iOSApp: App {
    @StateObject private var webSocketWorker = WebSocketWorker()

    init() {
        KoinKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
                    ContentView()
                        .environmentObject(webSocketWorker)
                }
    }
}
