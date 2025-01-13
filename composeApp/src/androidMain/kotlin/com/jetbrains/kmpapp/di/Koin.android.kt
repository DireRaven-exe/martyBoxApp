package com.jetbrains.kmpapp.di

import com.russhwolf.settings.ExperimentalSettingsApi
import org.koin.core.module.Module
import org.koin.dsl.module

@ExperimentalSettingsApi
actual fun platformModule(): Module = module {
    single { MultiplatformSettingsWrapper().createSettings() }
}