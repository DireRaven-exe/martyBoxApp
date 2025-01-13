package com.jetbrains.kmpapp.ui.screens.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.feature.datastore.AppPreferencesRepository
import com.jetbrains.kmpapp.utils.QrScanUiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QrCodeViewModel(
    private val appPreferencesRepository: AppPreferencesRepository
) : ViewModel() {
    private val _qrCodeUiState: MutableStateFlow<QrScanUiState> = MutableStateFlow(QrScanUiState())
    val qrCodeUiState: StateFlow<QrScanUiState> = _qrCodeUiState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        _qrCodeUiState.update { it.copy(isLoading = false, error = exception.message) }
    }

    fun onQrCodeDetected(result: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            appPreferencesRepository.saveQrCode(qrCode = result)
        }
        _qrCodeUiState.update { it.copy(detectedQR = result) }
    }
}
