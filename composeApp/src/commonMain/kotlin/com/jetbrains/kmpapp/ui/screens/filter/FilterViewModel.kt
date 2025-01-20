package com.jetbrains.kmpapp.ui.screens.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.feature.datastore.FilterRepository
import com.jetbrains.kmpapp.feature.datastore.OptionsRepository
import com.jetbrains.kmpapp.feature.datastore.SongsRepository
import com.jetbrains.kmpapp.utils.FilterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FilterViewModel(
    private val filterRepository: FilterRepository,
    private val optionsRepository: OptionsRepository,
    private val songsRepository: SongsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FilterUiState())
    val uiState: StateFlow<FilterUiState> = _uiState

    private val _optionsState = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val optionsState: StateFlow<Map<String, List<String>>> = _optionsState

    private val _availableKeys = MutableStateFlow<List<String>>(emptyList())
    val availableKeys: StateFlow<List<String>> = _availableKeys

    init {
        viewModelScope.launch {
            filterRepository.getFilters().collect { filters ->
                _uiState.value = _uiState.value.copy(selectedFilters = filters)
            }
        }

        viewModelScope.launch {
            filterRepository.getSearchByArtist().collect { artistSearchActive ->
                _uiState.value = _uiState.value.copy(artistSearchActive = artistSearchActive)
            }
        }

        viewModelScope.launch {
            filterRepository.getSearchByTitle().collect { titleSearchActive ->
                _uiState.value = _uiState.value.copy(titleSearchActive = titleSearchActive)
            }
        }

        viewModelScope.launch {
            optionsRepository.getAllKeys().collect { keys ->
                _availableKeys.value = keys
            }
        }
        addArtistsOptions()
    }

    fun fetchOptions(key: String) {
        viewModelScope.launch {
            optionsRepository.getOptionsForKey(key).collect { options ->
                _optionsState.value += (key to options)
            }
        }
    }

    fun updateFilter(key: String, value: String) {
        val currentFilters = _uiState.value.selectedFilters.toMutableMap()
        val updatedList = currentFilters[key]?.toMutableList() ?: mutableListOf()

        if (updatedList.contains(value)) {
            updatedList.remove(value)
        } else {
            updatedList.add(value)
        }

        currentFilters[key] = updatedList
        _uiState.value = _uiState.value.copy(selectedFilters = currentFilters)
        filterRepository.updateFilters(currentFilters)
    }

    fun addAllOptionsToFilter(key: String, values: List<String>) {
        val currentFilters = _uiState.value.selectedFilters.toMutableMap()
        val updatedList = currentFilters[key]?.toMutableSet() ?: mutableSetOf()

        updatedList.addAll(values)

        currentFilters[key] = updatedList.toList()
        _uiState.value = _uiState.value.copy(selectedFilters = currentFilters)
        filterRepository.updateFilters(currentFilters)
    }

    fun updateFilters(newFilters: Map<String, List<String>>) {
        filterRepository.updateFilters(newFilters)
    }

    fun updateSearch() {
        filterRepository.setSearchByArtist(_uiState.value.artistSearchActive)
        filterRepository.setSearchByTitle(_uiState.value.titleSearchActive)
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(selectedFilters = emptyMap())
        filterRepository.updateFilters(emptyMap())
    }

    fun getSectionTitle(key: String): String {
        return optionsRepository.getSectionTitle(key)
    }

    fun addArtistsOptions() {
        optionsRepository.addArtistsOptions(songsRepository.getArtists())
    }

    fun updateArtistSearchActive(value: Boolean) {
        _uiState.value = _uiState.value.copy(artistSearchActive = value)
        filterRepository.setSearchByArtist(value)
    }

    fun updateTitleSearchActive(value: Boolean) {
        _uiState.value = _uiState.value.copy(titleSearchActive = value)
        filterRepository.setSearchByTitle(value)
    }
}