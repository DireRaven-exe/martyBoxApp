//import Foundation
//import platform.Foundation.NSURLSession
//import platform.Foundation.NSURLSessionConfiguration
//import platform.BackgroundTasks.BGTaskScheduler
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
//        let webSocketManager = WebSocketManager.shared
//        webSocketManager.send("ping")
//
//        task.setTaskCompleted(success: true)
//    }
//}
