import ComposeApp
import SwiftUI
import BackgroundTasks

@main
struct iOSApp: App {
    //@StateObject private var webSocketWorker = WebSocketWorkerWrapper()

    init() {
        KoinKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
                    ContentView()
                        //.environmentObject(webSocketWorker)
                }
    }
}
