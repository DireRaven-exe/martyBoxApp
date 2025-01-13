@file:OptIn(ExperimentalSettingsApi::class)
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.jetbrains.kmpapp.di

import android.content.Context
import com.jetbrains.kmpapp.utils.ContextUtils
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings

actual class MultiplatformSettingsWrapper {
    private val context = ContextUtils.context

    actual fun createSettings(): ObservableSettings {
        val sharedPreferences =
            context.getSharedPreferences("martyboxapp_preferences", Context.MODE_PRIVATE)
        return AndroidSettings(delegate = sharedPreferences)
    }
}