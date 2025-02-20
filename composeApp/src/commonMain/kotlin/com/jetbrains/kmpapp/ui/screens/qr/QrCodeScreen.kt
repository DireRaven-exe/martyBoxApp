package com.jetbrains.kmpapp.ui.screens.qr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jetbrains.kmpapp.ui.navigation.NavigationItem
import com.jetbrains.kmpapp.utils.Constants.KEY_DEMO_CODE
import io.github.aakira.napier.Napier
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.openSettings
import martyboxapp.composeapp.generated.resources.requestPermission
import martyboxapp.composeapp.generated.resources.topBarScanQRcode
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.publicvalue.multiplatform.qrcode.CodeType
import org.publicvalue.multiplatform.qrcode.ScannerWithPermissions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeScreen(
    qrCodeViewModel: QrCodeViewModel = koinViewModel<QrCodeViewModel>(),
    navController: NavHostController,
    paddingValues: PaddingValues,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(Res.string.topBarScanQRcode))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        ScannerWithPermissions(
            modifier = Modifier.padding(0.dp),
            onScanned = {
                try {
                    if (it == KEY_DEMO_CODE) {
                        navController.navigate(NavigationItem.Demo.route)
                    } else {
                        if (navController.currentDestination?.route != NavigationItem.Main.route) {
                            Napier.d(
                                tag = "AndroidWebSocket",
                                message = "QrCodeScreen navigated to MainScreen"
                            )
                            navController.navigate(NavigationItem.Main.route)
                        }
                        qrCodeViewModel.onQrCodeDetected(it)
                    }
                } catch (e: Exception) {
                    // Обработка исключения
                    Napier.e(tag = "QrCodeScreen", message = "Error handling scanned QR code", throwable = e)
                    navController.popBackStack()
                }
                false // stop scanning
            },
            types = listOfNotNull(CodeType.QR),
            permissionDeniedContent = { state ->
                Surface(modifier = Modifier
                    .fillMaxSize()
                    .wrapContentWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            modifier = Modifier.padding(6.dp),
                            text =stringResource(Res.string.requestPermission),
                            maxLines = 1
                        )
                        Button(
                            onClick = { state.goToSettings() },
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                text = stringResource(Res.string.openSettings),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        )
    }
}
