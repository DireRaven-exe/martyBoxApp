@file:OptIn(ExperimentalSettingsApi::class)
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.jetbrains.kmpapp.di

import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import platform.Foundation.NSUserDefaults

actual class MultiplatformSettingsWrapper {
    actual fun createSettings(): ObservableSettings {
        val nsUserDefault = NSUserDefaults.standardUserDefaults
        return AppleSettings(delegate = nsUserDefault)
    }
}