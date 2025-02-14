package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.data.sockets.WebSocketService
import com.jetbrains.kmpapp.feature.sockets.AndroidWebSocketService
import com.jetbrains.kmpapp.feature.sockets.WebSocketWorker
import com.russhwolf.settings.ExperimentalSettingsApi
import org.koin.core.module.Module
import org.koin.dsl.module

@ExperimentalSettingsApi
actual fun platformModule(): Module = module {
    single { MultiplatformSettingsWrapper().createSettings() }

    single<WebSocketService> { AndroidWebSocketService() }
    single { WebSocketWorker }
}