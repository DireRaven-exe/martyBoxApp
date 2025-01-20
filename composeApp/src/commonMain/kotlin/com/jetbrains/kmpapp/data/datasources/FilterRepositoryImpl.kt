package com.jetbrains.kmpapp.data.datasources

import com.jetbrains.kmpapp.feature.datastore.FilterRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FilterRepositoryImpl: FilterRepository {
    private val _filters = MutableStateFlow<Map<String, List<String>>>(emptyMap())

    private val _searchByArtist = MutableStateFlow(true)

    private val _searchByTitle = MutableStateFlow(true)

    override fun getFilters(): Flow<Map<String, List<String>>> = _filters

    override fun updateFilters(newFilters: Map<String, List<String>>) {
        _filters.value = newFilters
    }

    override fun getSearchByArtist(): Flow<Boolean> = _searchByArtist

    override fun getSearchByTitle(): Flow<Boolean> = _searchByTitle

    override fun setSearchByArtist(searchByArtist: Boolean) {
        _searchByArtist.value = searchByArtist
    }

    override fun setSearchByTitle(searchByTitle: Boolean) {
        Napier.e(tag = "FilterRepositoryImpl", message = "setSearchByTitle: $searchByTitle")
        _searchByTitle.value = searchByTitle
    }
}