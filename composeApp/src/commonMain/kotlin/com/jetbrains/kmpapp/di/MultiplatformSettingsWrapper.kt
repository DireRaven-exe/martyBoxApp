@file:OptIn(ExperimentalSettingsApi::class)
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.jetbrains.kmpapp.di

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings

expect class MultiplatformSettingsWrapper {
    fun createSettings(): ObservableSettings
}