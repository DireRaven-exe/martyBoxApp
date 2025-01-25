package com.jetbrains.kmpapp.di

expect class AppStateProvider() {
    fun isAppInBackground(): Boolean
}
