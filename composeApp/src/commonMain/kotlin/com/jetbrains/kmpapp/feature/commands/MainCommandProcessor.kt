package com.jetbrains.kmpapp.feature.commands

import com.jetbrains.kmpapp.ui.screens.main.MainViewModel

class MainCommandProcessor(mainViewModel: MainViewModel) : BaseCommandProcessor(mainViewModel) {
    private val mainViewModel = viewModel as MainViewModel

    fun connectToWebSocket(qrCode: String?) {
        if (!qrCode.isNullOrEmpty()) {
            mainViewModel.connectToWebSocket(qrCode)
        }
    }

    fun updateCurrentTable(table: Int) {
        mainViewModel.updateCurrentTable(table)
        mainViewModel.saveCurrentTable(table)
    }

    fun clearData() {
        mainViewModel.clearSavedQrCode()
        mainViewModel.clearSavedTableNumber()
    }
}