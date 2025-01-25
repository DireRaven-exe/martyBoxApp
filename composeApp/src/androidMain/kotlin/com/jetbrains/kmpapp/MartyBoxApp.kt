package com.jetbrains.kmpapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.datatransport.BuildConfig
import com.jetbrains.kmpapp.di.initKoin
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