@file:OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)

package com.jetbrains.kmpapp.data.datasources

import com.jetbrains.kmpapp.feature.datastore.AppPreferencesRepository
import com.jetbrains.kmpapp.utils.Constants.KEY_QRCODE
import com.jetbrains.kmpapp.utils.Constants.KEY_TABLE_NUMBER
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getIntOrNullFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

class AppPreferencesRepositoryImpl(
    private val observableSettings: ObservableSettings
) : AppPreferencesRepository {
    override suspend fun saveQrCode(qrCode: String) {
        observableSettings.putString(key = KEY_QRCODE, value = qrCode)
    }

    override suspend fun saveTableNumber(tableNumber: Int) {
        observableSettings.putInt(key = KEY_TABLE_NUMBER, value = tableNumber)
    }

    override suspend fun getQrCode(): Flow<String?> {
        return observableSettings.getStringOrNullFlow(key = KEY_QRCODE)
    }

    override suspend fun getTableNumber(): Flow<Int?> {
        return observableSettings.getIntOrNullFlow(key = KEY_TABLE_NUMBER)
    }
}
