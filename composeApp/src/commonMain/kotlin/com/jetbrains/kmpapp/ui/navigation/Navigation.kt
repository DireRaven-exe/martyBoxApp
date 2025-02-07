package com.jetbrains.kmpapp.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jetbrains.kmpapp.ui.screens.home.HomeScreen
import com.jetbrains.kmpapp.ui.screens.main.MainScreen
import com.jetbrains.kmpapp.ui.screens.qr.QrCodeScreen

@Composable
fun Navigation(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(navController = navController, startDestination = NavigationItem.Home.route) {
        composable(NavigationItem.QRCode.route) {
            QrCodeScreen(navController = navController, paddingValues = paddingValues)
        }
        composable(NavigationItem.Home.route) {
            HomeScreen(navController = navController, paddingValues = paddingValues)
        }
        composable(NavigationItem.Main.route) {
            MainScreen(navController = navController, paddingValues = paddingValues)
        }
    }
}