package com.jetbrains.kmpapp.data.datasources

import com.jetbrains.kmpapp.feature.datastore.OptionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class OptionsRepositoryImpl: OptionsRepository {
    private val sectionTitles = mapOf(
        "artist" to "Исполнитель"
    )

    private val optionsFlow = MutableStateFlow(
        mapOf(
            "artist" to emptyList<String>()
        )
    )

    override fun getOptionsForKey(key: String): Flow<List<String>> {
        return optionsFlow.map { it[key] ?: emptyList() }
    }

    override fun getAllKeys(): Flow<List<String>> {
        return flowOf(optionsFlow.value.keys.toList())
    }

    override fun getSectionTitle(key: String): String {
        return sectionTitles[key] ?: key
    }

    override fun addArtistsOptions(options: List<String>) {
        optionsFlow.value += ("artist" to options)
    }
}
