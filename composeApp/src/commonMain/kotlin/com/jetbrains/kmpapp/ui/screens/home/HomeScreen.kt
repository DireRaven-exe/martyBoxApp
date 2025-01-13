package com.jetbrains.kmpapp.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jetbrains.kmpapp.MainRes
import com.jetbrains.kmpapp.ui.theme.logoTint
import io.github.skeptick.libres.compose.painterResource
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = koinInject()
) {
    val uiState = homeViewModel.homeUiState.collectAsState().value
    val savedQrCode = uiState.savedQrCode

    LaunchedEffect(savedQrCode) {
        savedQrCode?.let {
            if (it.isNotEmpty()) {
                navController.navigate("main_screen")
            }
        }
    }
    if (uiState.savedQrCode.isEmpty()) {

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
                        painter = painterResource(MainRes.image.martin_player_logo),
                        contentDescription = MainRes.string.app_name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(logoTint)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = MainRes.string.scanQRcode,
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Button(
                        onClick = {
                            navController.navigate("qr_code_screen")
                        },
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(MainRes.image.qr_code),
                                contentDescription = "QR Code",
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = MainRes.string.scan,
                                style = MaterialTheme.typography.titleMedium,
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

//    BackHandler {
//        exitProcess(0)
//    }
}