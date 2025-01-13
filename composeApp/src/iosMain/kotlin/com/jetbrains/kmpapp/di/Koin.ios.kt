@file:OptIn(ExperimentalSettingsApi::class)

package com.jetbrains.kmpapp.di

import com.russhwolf.settings.ExperimentalSettingsApi
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { MultiplatformSettingsWrapper().createSettings() }
}