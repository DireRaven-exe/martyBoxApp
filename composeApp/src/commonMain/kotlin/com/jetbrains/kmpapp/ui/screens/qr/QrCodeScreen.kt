package com.jetbrains.kmpapp.ui.screens.qr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.openSettings
import martyboxapp.composeapp.generated.resources.requestPermission
import martyboxapp.composeapp.generated.resources.topBarScanQRcode
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.publicvalue.multiplatform.qrcode.CodeType
import org.publicvalue.multiplatform.qrcode.ScannerWithPermissions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeScreen(
    qrCodeViewModel: QrCodeViewModel = koinInject(),
    navController: NavHostController,
) {
    val uiState by qrCodeViewModel.qrCodeUiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(Res.string.topBarScanQRcode))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
                navController.navigate("main_screen")
                qrCodeViewModel.onQrCodeDetected(it)
                false // stop scanning
            },
            types = listOf(CodeType.QR),
            permissionDeniedContent = { state ->
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            modifier = Modifier.padding(6.dp),
                            text =stringResource(Res.string.requestPermission)
                        )
                        Button(onClick = { state.goToSettings() }) {
                            Text(stringResource(Res.string.openSettings))
                        }
                    }
                }
            }
        )
    }
}
