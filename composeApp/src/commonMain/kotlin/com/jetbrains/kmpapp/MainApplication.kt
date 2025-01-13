package com.jetbrains.kmpapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jetbrains.kmpapp.ui.screens.home.HomeScreen
import com.jetbrains.kmpapp.ui.screens.main.MainScreen
import com.jetbrains.kmpapp.ui.screens.qr.QrCodeScreen
import com.jetbrains.kmpapp.ui.theme.MartinBoxAppTheme

@Composable
fun MainApplication() {
    MartinBoxAppTheme {
        val navController: NavHostController = rememberNavController()
        NavHost(navController, startDestination = "home_screen") {
            composable("qr_code_screen") {
                QrCodeScreen(navController = navController)
            }
            composable("home_screen") {
                HomeScreen(navController = navController)
            }
            composable("main_screen") {
                MainScreen(navController = navController)
            }
        }
    }
}
