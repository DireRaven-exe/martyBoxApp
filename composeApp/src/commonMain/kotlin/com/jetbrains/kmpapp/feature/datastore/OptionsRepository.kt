package com.jetbrains.kmpapp.feature.datastore

import kotlinx.coroutines.flow.Flow

interface OptionsRepository {
    fun getOptionsForKey(key: String): Flow<List<String>>

    fun getAllKeys(): Flow<List<String>>

    fun getSectionTitle(key: String): String
    fun addArtistsOptions(options: List<String>)
}
