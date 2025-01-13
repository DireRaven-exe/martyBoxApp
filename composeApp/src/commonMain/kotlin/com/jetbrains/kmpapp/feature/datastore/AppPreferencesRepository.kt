package com.jetbrains.kmpapp.feature.datastore

import kotlinx.coroutines.flow.Flow

interface AppPreferencesRepository {

    suspend fun saveQrCode(qrCode: String)
    suspend fun saveTableNumber(tableNumber: Int)

    suspend fun getQrCode(): Flow<String?>

    suspend fun getTableNumber(): Flow<Int?>
}
