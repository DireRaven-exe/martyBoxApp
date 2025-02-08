import SwiftUI
import ComposeApp
import BackgroundTasks


@main
struct iOSApp: App {
    init() {
        KoinKt.doInitKoin()

        BGTaskScheduler.shared.register(forTaskWithIdentifier: WebSocketBackgroundTask.identifier, using: nil) { task in
            WebSocketBackgroundTask().handle(task: task)
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
            .onAppear {
                WebSocketBackgroundTask().schedule()
            }
        }
    }
}
