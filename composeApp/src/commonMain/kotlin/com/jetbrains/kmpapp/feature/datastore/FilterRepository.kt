package com.jetbrains.kmpapp.feature.datastore

import kotlinx.coroutines.flow.Flow

interface FilterRepository {

    fun getFilters(): Flow<Map<String, List<String>>>
    fun updateFilters(newFilters: Map<String, List<String>>)
    fun getSearchByArtist(): Flow<Boolean>
    fun getSearchByTitle(): Flow<Boolean>
    fun setSearchByTitle(searchByTitle: Boolean)
    fun setSearchByArtist(searchByArtist: Boolean)
}
