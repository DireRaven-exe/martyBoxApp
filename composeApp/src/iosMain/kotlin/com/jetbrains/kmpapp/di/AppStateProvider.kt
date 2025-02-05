package com.jetbrains.kmpapp.di

// iOS реализация
actual class AppStateProvider actual constructor() {
    private var isInBackground = false

//    init {
//        UIApplication.sharedApplication.applicationState.addObserver(object : NSObject(), UIApplicationStateObserverProtocol {
//            override fun applicationDidEnterBackground(application: UIApplication) {
//                isInBackground = true
//            }
//
//            override fun applicationWillEnterForeground(application: UIApplication) {
//                isInBackground = false
//            }
//        })
//    }

    actual fun isAppInBackground(): Boolean {
        return isInBackground
    }
}