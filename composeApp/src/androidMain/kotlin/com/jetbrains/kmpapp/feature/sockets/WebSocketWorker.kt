package com.jetbrains.kmpapp.feature.sockets

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.jetbrains.kmpapp.R
import com.jetbrains.kmpapp.data.sockets.WebSocketManager
import com.jetbrains.kmpapp.feature.datastore.AppPreferencesRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class WebSocketService : Service() {

    private var webSocketManager: WebSocketManager? = null
    private lateinit var appPreferencesRepository: AppPreferencesRepository
    private var webSocketJob: Job? = null

    @OptIn(ExperimentalSettingsApi::class)
    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        startForegroundService()
        webSocketManager = WebSocketManager.getInstance()
        appPreferencesRepository = get()
        observeQrCodeAndConnect()
    }

    private fun observeQrCodeAndConnect() {
        webSocketJob?.cancel() // Остановим предыдущее подключение
        webSocketJob = CoroutineScope(Dispatchers.IO).launch {
            appPreferencesRepository.getQrCode()
                .filterNotNull() // Пропускаем null-значения
                .collect { qrCode ->
                    connectToWebSocket(qrCode)
                }
        }
    }

    private fun connectToWebSocket(qrCode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            webSocketManager?.disconnect() // Отключаем старое соединение (если есть)
            delay(1000) // Даем небольшую задержку перед новым подключением
            webSocketManager?.connect(qrCode)
        }
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, "WebSocketChannel")
            .setContentTitle("WebSocket Service")
            .setContentText("Ожидание подключения")
            .setSmallIcon(R.drawable.notifications)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(1, notification)
        Log.d("WebSocketService", "Starting foreground service with notification")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "WebSocketChannel",
            "WebSocket Service Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for WebSocket service notifications"
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        Log.d("WebSocketService", "Notification channel created")
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketJob?.cancel()
        CoroutineScope(Dispatchers.IO).launch {
            webSocketManager?.disconnect()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, WebSocketService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}

