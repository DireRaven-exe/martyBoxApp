package com.jetbrains.kmpapp

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.datatransport.BuildConfig
import com.jetbrains.kmpapp.di.initKoin
import com.jetbrains.kmpapp.feature.sockets.WebSocketWorker
import com.jetbrains.kmpapp.utils.ContextUtils
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class MartyBoxApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextUtils.setContext(context = this)
        initKoin {
            androidLogger(level = if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(androidContext = this@MartyBoxApp)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.areNotificationsEnabled()) {
                // Запросить разрешение на уведомления
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }

        // Запуск сервиса
        WebSocketWorker.start(this@MartyBoxApp)

        // Отслеживание жизненного цикла приложения
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver())
    }

    inner class AppLifecycleObserver : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            // Приложение перешло в foreground
            WebSocketWorker.start(this@MartyBoxApp)
        }

        override fun onStop(owner: LifecycleOwner) {
            // Приложение перешло в background, но не обязательно завершено
            // Здесь ничего не делаем, так как сервис должен продолжать работать
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            WebSocketWorker.stop(this@MartyBoxApp)
        }
    }

    companion object {
        private var instance: MartyBoxApp? = null

        fun getAppContext(): Context {
            return instance!!.applicationContext
        }

        fun getLifecycleOwner(): LifecycleOwner {
            return ProcessLifecycleOwner.get()
        }
    }
}
