package com.jetbrains.kmpapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.feature.datastore.AppPreferencesRepository
import com.jetbrains.kmpapp.utils.HomeUiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val appPreferencesRepository: AppPreferencesRepository
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

//    val uiState = appPreferencesRepository.qrCode.map { HomeUiState(savedQrCode = it) }
//        .stateIn(viewModelScope, SharingStarted.Lazily, HomeUiState())

    //val savedQrCode: Flow<String?> = appPreferencesRepository.qrCode
        //.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        _homeUiState.update { it.copy(isLoading = false, error = exception.message) }
    }

    init {
        getQrCode()
    }

    fun getQrCode() = viewModelScope.launch(coroutineExceptionHandler) {
        appPreferencesRepository.getQrCode().collect { qrCode ->
            qrCode?.let {
                _homeUiState.update { it.copy(savedQrCode = qrCode) }
            }
        }
    }
}
