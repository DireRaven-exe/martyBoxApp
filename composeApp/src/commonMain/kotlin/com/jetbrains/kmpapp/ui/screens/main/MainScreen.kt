package com.jetbrains.kmpapp.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.jetbrains.kmpapp.data.sockets.WebSocketConnectionState
import com.jetbrains.kmpapp.feature.backhandler.OnBackPressedHandler
import com.jetbrains.kmpapp.feature.commands.CommandHandler
import com.jetbrains.kmpapp.ui.components.content.Picker
import com.jetbrains.kmpapp.ui.components.content.rememberPickerState
import com.jetbrains.kmpapp.ui.components.dialogs.ConfirmDisconnectionDialog
import com.jetbrains.kmpapp.ui.components.dialogs.ServerDisconnectedDialog
import com.jetbrains.kmpapp.ui.navigation.NavigationItem
import com.jetbrains.kmpapp.ui.screens.loading.LoadingScreen
import com.jetbrains.kmpapp.ui.screens.main.views.ClubTypeView
import com.jetbrains.kmpapp.ui.screens.main.views.HomeTypeView
import com.jetbrains.kmpapp.ui.theme.buttonAcceptDialog
import com.jetbrains.kmpapp.utils.Constants
import io.github.aakira.napier.Napier
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.accept
import martyboxapp.composeapp.generated.resources.indicateTableNumber
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun MainScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel = koinInject(),
    paddingValues: PaddingValues
) {
    val uiState = mainViewModel.mainUiState.collectAsState().value
    var savedTableValue by rememberSaveable { mutableStateOf(uiState.currentTable) }
    val savedQrCode = uiState.savedQrCode
    var showTableDialog = false
    var showDisconnectedDialog by rememberSaveable { mutableStateOf(false) }
    var showConfirmDisconnectionDialog = false
    var isManuallyDisconnected by rememberSaveable { mutableStateOf(false) }
    var isReconnectAllowed  by rememberSaveable { mutableStateOf(true) }


    val commandHandler = CommandHandler(mainViewModel)
    val valuesPickerState = rememberPickerState()

    // Сохраняем qrCode при первом получении
    LaunchedEffect(savedQrCode) {
        savedQrCode.let {
            if (isReconnectAllowed) {
                Napier.d(tag = "Websocket", message = it)
                mainViewModel.connectToWebSocket(it)
            }
        }
    }

    LaunchedEffect(Unit) {
        mainViewModel.getCurrentTable()
    }

    LaunchedEffect(uiState.currentTable) {
        mainViewModel.getCurrentTable()
        savedTableValue = uiState.currentTable
    }

    Napier.e(tag = "SAVEDTABLE", message = "TABLE IN MAIN: ${uiState.currentTable}")

//    LaunchedEffect(uiState.isServerConnected) {
//        if (!uiState.isServerConnected && navController.currentBackStackEntry?.destination?.route != NavigationItem.Home.route) {
//            showDisconnectedDialog = true
//        }
//    }

    LaunchedEffect(mainViewModel.connectionState) {
        mainViewModel.connectionState.collect { state ->
            if (state == WebSocketConnectionState.Disconnected) {
                showDisconnectedDialog = true
            } else {
                showDisconnectedDialog = false
            }
        }
    }

    LaunchedEffect(savedTableValue) {
        mainViewModel.updateCurrentTable(savedTableValue)
        if (savedTableValue == -1 && uiState.isServerConnected) {
            showTableDialog = true
        }
    }

    OnBackPressedHandler(
        isServerConnected = uiState.isServerConnected,
        isLoading = uiState.isLoading,
        onBackFromServerConnected = {
            showConfirmDisconnectionDialog = true
        },
        onBackFromLoading = {
            mainViewModel.clearSavedQrCode()
            mainViewModel.clearSavedTableNumber()
            navController.navigate("home_screen") {
                popUpTo("home_screen") { inclusive = true }
            }
        },
        onBackDefault = {
            showConfirmDisconnectionDialog = true
        }
    )

    if (showDisconnectedDialog && !isManuallyDisconnected) {
        ServerDisconnectedDialog(
            onDisconnect = {
                showDisconnectedDialog = false
                isReconnectAllowed = false
                mainViewModel.clearSavedQrCode()
                mainViewModel.clearSavedTableNumber()
                navController.navigate(NavigationItem.Home.route) {
                    popUpTo(NavigationItem.Home.route) { inclusive = true }
                }
                mainViewModel.onDisconnected("Connection lost")
            }
        )

    }

    if (showConfirmDisconnectionDialog) {
        ConfirmDisconnectionDialog(
            onConfirm = {
                showConfirmDisconnectionDialog = false
                isManuallyDisconnected = true
                isReconnectAllowed = false
                mainViewModel.clearSavedQrCode()
                mainViewModel.clearSavedTableNumber()
                navController.navigate(NavigationItem.Home.route) {
                    popUpTo(NavigationItem.Home.route) { inclusive = true }
                }
                mainViewModel.onDisconnected("User manually disconnected")
            },
            onDismiss = {
                showConfirmDisconnectionDialog = false
            }
        )
    }

    if (uiState.isLoading) {
        LoadingScreen(
            onCancelClick = {
                isManuallyDisconnected = true
                isReconnectAllowed = false
                mainViewModel.clearSavedQrCode()
                mainViewModel.clearSavedTableNumber()
                navController.navigate(NavigationItem.Home.route) {
                    popUpTo(NavigationItem.Home.route) { inclusive = true }
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
                    paddingValues = paddingValues,
                )
            }
            Constants.TYPE_CLUB -> {
                if (savedTableValue != -1) {
                    ClubTypeView(
                        uiState = uiState,
                        commandHandler = commandHandler,
                        paddingValues = paddingValues,
                    )
                } else {
                    showTableDialog = true
                }
            }
        }
    }

    if (showTableDialog && uiState.isServerConnected) {
        Dialog(onDismissRequest = { showTableDialog = false }) {
            Card {
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
                        colors = ButtonDefaults.buttonColors(containerColor = buttonAcceptDialog),
                        modifier = Modifier
                            .padding(16.dp)
                            .wrapContentWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.accept),
                            style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                            maxLines = 1

                        )
                    }
                }
            }
        }
    }
}
