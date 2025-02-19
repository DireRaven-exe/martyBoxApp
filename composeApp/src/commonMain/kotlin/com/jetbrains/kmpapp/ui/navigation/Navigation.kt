package com.jetbrains.kmpapp.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jetbrains.kmpapp.ui.screens.demo.DemoScreen
import com.jetbrains.kmpapp.ui.screens.home.HomeScreen
import com.jetbrains.kmpapp.ui.screens.main.MainScreen
import com.jetbrains.kmpapp.ui.screens.qr.QrCodeScreen
import io.github.aakira.napier.Napier

@Composable
fun Navigation(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(navController = navController, startDestination = NavigationItem.Home.route) {
        composable(route = NavigationItem.QRCode.route) {
            Napier.d(tag = "AndroidWebSocket", message = "QrCodeScreen open from ${navController.currentDestination?.route}")
            QrCodeScreen(navController = navController, paddingValues = paddingValues)
        }
        composable(route = NavigationItem.Home.route) {
            Napier.d(tag = "AndroidWebSocket", message = "HomeScreen open from ${navController.currentDestination?.route}")
            HomeScreen(navController = navController, paddingValues = paddingValues)
        }
        composable(route = NavigationItem.Main.route) {
            Napier.d(tag = "AndroidWebSocket", message = "MainScreen open from ${navController.previousBackStackEntry?.destination?.route}")
            MainScreen(navController = navController, paddingValues = paddingValues)
        }

        composable(route = NavigationItem.Demo.route) {
            DemoScreen(navController = navController, paddingValues = paddingValues)
        }
    }
}