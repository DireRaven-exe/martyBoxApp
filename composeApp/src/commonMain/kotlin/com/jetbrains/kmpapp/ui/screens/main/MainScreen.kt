package com.jetbrains.kmpapp.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.jetbrains.kmpapp.feature.backhandler.OnBackPressedHandler
import com.jetbrains.kmpapp.feature.commands.CommandHandler
import com.jetbrains.kmpapp.ui.components.ConfirmDisconnectionDialog
import com.jetbrains.kmpapp.ui.components.Picker
import com.jetbrains.kmpapp.ui.components.ServerDisconnectedDialog
import com.jetbrains.kmpapp.ui.components.rememberPickerState
import com.jetbrains.kmpapp.ui.screens.main.views.ClubTypeView
import com.jetbrains.kmpapp.ui.screens.main.views.HomeTypeView
import com.jetbrains.kmpapp.utils.Constants
import io.github.aakira.napier.Napier
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.accept
import martyboxapp.composeapp.generated.resources.attempting_to_connect
import martyboxapp.composeapp.generated.resources.cancel
import martyboxapp.composeapp.generated.resources.indicateTableNumber
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun MainScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel = koinInject()
) {
    val uiState = mainViewModel.mainUiState.collectAsState().value
    val savedTableValue = uiState.currentTable
    val savedQrCode = uiState.savedQrCode
    var showTableDialog = false
    var showDisconnectedDialog by rememberSaveable { mutableStateOf(false) }
    var showConfirmDisconnectionDialog = false
    var isManuallyDisconnected by rememberSaveable { mutableStateOf(false) }
    var isReconnectAllowed = true
    var isLoadingData by remember { mutableStateOf(false) } // Состояние для отслеживания загрузки данных

    val commandHandler = CommandHandler(mainViewModel)
    val valuesPickerState = rememberPickerState()

    // Сохраняем qrCode при первом получении
    LaunchedEffect(savedQrCode) {
        savedQrCode?.let {
            if (isReconnectAllowed) {
                mainViewModel.connectToWebSocket(it)
            }
        }
    }

    LaunchedEffect(uiState.isServerConnected) {
        if (!uiState.isServerConnected && navController.currentBackStackEntry?.destination?.route != "home_screen") {
            showDisconnectedDialog = true
        }
    }

    LaunchedEffect(savedTableValue) {
        mainViewModel.updateCurrentTable(savedTableValue ?: -1)
        if (savedTableValue == -1 && uiState.isServerConnected) {
            showTableDialog = true
        }
    }

    if (showDisconnectedDialog && !isManuallyDisconnected) {
        ServerDisconnectedDialog(
            onDisconnect = {
                showDisconnectedDialog = false
                isReconnectAllowed = false
                mainViewModel.clearSavedQrCode()
                mainViewModel.clearSavedTableNumber()
                navController.navigate("home_screen") {
                    popUpTo("home_screen") { inclusive = true }
                }
                mainViewModel.onDisconnected("Connection lost")
            },
            onReconnect = {
                showDisconnectedDialog = false
                isReconnectAllowed = true
                mainViewModel.updateIsLoading(true)
                mainViewModel.connectToWebSocket(savedQrCode)
            }
        )

    }

    OnBackPressedHandler(
        isServerConnected = uiState.isServerConnected,
        isLoading = uiState.isLoading,
        onBackFromLoading = {
            // Возврат на главный экран при загрузке
            mainViewModel.clearSavedQrCode()
            mainViewModel.clearSavedTableNumber()
            navController.navigate("home_screen") {
                popUpTo("home_screen") { inclusive = true }
            }
        },
        onBackFromServerConnected = {
            showConfirmDisconnectionDialog = true
        },
        onBackDefault = {
            // Обычное поведение кнопки назад
            mainViewModel.clearSavedQrCode()
            mainViewModel.clearSavedTableNumber()
            navController.navigate("home_screen") {
                popUpTo("home_screen") { inclusive = true }
            }
            mainViewModel.onDisconnected("the back button was pressed")
        }
    )

    if (showConfirmDisconnectionDialog) {
        ConfirmDisconnectionDialog(
            onConfirm = {
                showConfirmDisconnectionDialog = false
                isManuallyDisconnected = true
                isReconnectAllowed = false
                mainViewModel.clearSavedQrCode()
                mainViewModel.clearSavedTableNumber()
                navController.navigate("home_screen") {
                    popUpTo("home_screen") { inclusive = true }
                }
                mainViewModel.onDisconnected("User manually disconnected")
            },
            onDismiss = {
                showConfirmDisconnectionDialog = false
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Napier.i("MainScreen: ${uiState.isLoading}, ${uiState.isServerConnected}")
        if ((uiState.isLoading)) {
            LoadingScreen(
                onCancelClick = {
                    isManuallyDisconnected = true
                    isReconnectAllowed = false
                    mainViewModel.clearSavedQrCode()
                    mainViewModel.clearSavedTableNumber()
                    navController.navigate("home_screen") {
                        popUpTo("home_screen") { inclusive = true }
                    }
                    mainViewModel.onDisconnected("")
                }
            )
        } else {
            when (uiState.serverData.type) {
                Constants.TYPE_HOME -> {
                    HomeTypeView(
                        uiState = uiState,
                        commandHandler = commandHandler,
                    )
                }
                Constants.TYPE_CLUB -> {
                    if (savedTableValue != -1) {
                        ClubTypeView(
                            uiState = uiState,
                            commandHandler = commandHandler
                        )
                    } else {
                        showTableDialog = true
                    }
                }
            }
        }

        if (showTableDialog && uiState.isServerConnected) {
            Dialog(onDismissRequest = { showTableDialog = false }) {
                Card() {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.indicateTableNumber),
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Picker(
                            state = valuesPickerState,
                            items = (1..uiState.serverData.tables).map { it.toString() },
                            visibleItemsCount = 5,
                            modifier = Modifier.fillMaxWidth(0.5f),
                            textModifier = Modifier.padding(8.dp),
                            textStyle = TextStyle(fontSize = 32.sp),
                            dividerColor = Color(0xFFE8E8E8)
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                val table = valuesPickerState.selectedItem.toIntOrNull() ?: -1
                                mainViewModel.updateCurrentTable(table)
                                mainViewModel.saveCurrentTable(table)
                                showTableDialog = false

                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = stringResource(Res.string.accept))
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun LoadingScreen(onCancelClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(Res.string.attempting_to_connect),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CircularProgressIndicator(
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(onClick = onCancelClick) {
                Text(text = stringResource(Res.string.cancel))
            }
        }
    }
}