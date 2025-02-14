@file:OptIn(ExperimentalSettingsApi::class)

package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.data.datasources.AppPreferencesRepositoryImpl
import com.jetbrains.kmpapp.data.sockets.KtorWebsocketClient
import com.jetbrains.kmpapp.data.sockets.WebSocketManager
import com.jetbrains.kmpapp.feature.datastore.AppPreferencesRepository
import com.jetbrains.kmpapp.ui.screens.home.HomeViewModel
import com.jetbrains.kmpapp.ui.screens.main.MainViewModel
import com.jetbrains.kmpapp.ui.screens.qr.QrCodeViewModel
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin

fun commonModule() = module {
    single {
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            install(ContentNegotiation) {
                json(json, contentType = ContentType.Application.Json)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10000 // Пример настройки тайм-аута
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }

    single { AppStateProvider() }
    single { KtorWebsocketClient() }
    single { WebSocketManager.getInstance() }
    single<AppPreferencesRepository> {
        val settings = get<ObservableSettings>()
        AppPreferencesRepositoryImpl(observableSettings = settings)
    }

    factory { HomeViewModel(appPreferencesRepository = get()) }
    factory { MainViewModel(
        appPreferencesRepository = get(),
        webSocketService = get()
    ) }
    factory { QrCodeViewModel(appPreferencesRepository = get()) }
}

//fun initKoin() {//enableNetworkLogs: Boolean = true, appDeclaration: KoinAppDeclaration = {}) =
//    startKoin {
//        modules(sharedModules())
//}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        modules(commonModule(), platformModule())
        appDeclaration()
    }

// called by iOS client
fun initKoin() = initKoin {}

expect fun platformModule(): Module

fun getAppPreferencesRepository(): AppPreferencesRepository {
    return getKoin().get()
}