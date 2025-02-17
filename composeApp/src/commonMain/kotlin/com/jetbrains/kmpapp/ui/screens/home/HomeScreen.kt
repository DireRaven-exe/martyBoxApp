package com.jetbrains.kmpapp.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jetbrains.kmpapp.ui.navigation.NavigationItem
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.ui.theme.buttonAcceptDialog
import io.github.aakira.napier.Napier
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.app_name
import martyboxapp.composeapp.generated.resources.martinlogo
import martyboxapp.composeapp.generated.resources.qr_code
import martyboxapp.composeapp.generated.resources.scan
import martyboxapp.composeapp.generated.resources.scanQRcode
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = koinViewModel<HomeViewModel>(),
    paddingValues: PaddingValues
) {
    val uiState = homeViewModel.homeUiState.collectAsState().value
    var savedQrCode = uiState.savedQrCode

    if (savedQrCode.isNotEmpty()) {
        Napier.d(tag = "AndroidWebSocket", message = "HomeScreen navigated to MainScreen")
        navController.navigate(NavigationItem.Main.route)
    } else {
        Surface {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(Res.drawable.martinlogo),
                        contentDescription = stringResource(Res.string.app_name),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Fit,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = stringResource(Res.string.scanQRcode),
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Button(
                        onClick = { navController.navigate(NavigationItem.QRCode.route) },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonAcceptDialog),
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.qr_code),
                            contentDescription = "QR Code",
                            modifier = Modifier.size(24.dp),
                            tint = LocalCustomColorsPalette.current.primaryText
                        )
                        Text(
                            text = stringResource(Res.string.scan),
                            style = MaterialTheme.typography.bodySmall
                                .copy(color = LocalCustomColorsPalette.current.primaryText),
                            maxLines = 1,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                }
            }
        }
    }
}
