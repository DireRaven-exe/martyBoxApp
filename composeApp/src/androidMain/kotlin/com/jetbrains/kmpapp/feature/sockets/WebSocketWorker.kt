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
import com.jetbrains.kmpapp.data.sockets.WebSocketService
import com.jetbrains.kmpapp.feature.datastore.AppPreferencesRepository
import kotlinx.coroutines.Job
import org.koin.android.ext.android.get


class WebSocketWorker : Service() {
    private lateinit var appPreferencesRepository: AppPreferencesRepository
    private var webSocketJob: Job? = null

    private lateinit var webSocketService: WebSocketService

    override fun onCreate() {
        super.onCreate()
        Log.d("WebSocketService", "onCreate called")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        webSocketService = AndroidWebSocketService()
        startForegroundService()
        appPreferencesRepository = get()
    }

    private fun updateNotification(title: String, content: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
        val notification = NotificationCompat.Builder(this, "WebSocketChannel")
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.notifications)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, "WebSocketChannel")
            .setContentTitle("MARTIN Catalog")
            .setContentText("Инициализация выполнена")
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
        Log.d("WebSocketService", "onDestroy called")
        webSocketJob?.cancel()
        webSocketService.disconnect()
        updateNotification("MARTIN Catalog", "Соединение разорвано")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, WebSocketWorker::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, WebSocketWorker::class.java)
            context.stopService(intent)
        }

        fun restart(context: Context, url: String) {
            stop(context) // Остановить текущий экземпляр сервиса
            val intent = Intent(context, WebSocketWorker::class.java).apply {
                putExtra("url", url) // Передать URL
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent) // Перезапуск сервиса
            } else {
                context.startService(intent)
            }
        }
    }
}

