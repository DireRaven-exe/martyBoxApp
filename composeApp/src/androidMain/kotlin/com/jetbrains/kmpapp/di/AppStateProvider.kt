package com.jetbrains.kmpapp.di

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.jetbrains.kmpapp.MartyBoxApp

actual class AppStateProvider actual constructor() {
    private var isInBackground = false

    init {
        val lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                isInBackground = true
            }

            override fun onResume(owner: LifecycleOwner) {
                isInBackground = false
            }
        }

        // Используйте ProcessLifecycleOwner
        val lifecycleOwner = MartyBoxApp.getLifecycleOwner()
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    }

    actual fun isAppInBackground(): Boolean {
        return isInBackground
    }
}