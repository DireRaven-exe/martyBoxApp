package com.jetbrains.kmpapp.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jetbrains.kmpapp.ui.screens.home.HomeScreen
import com.jetbrains.kmpapp.ui.screens.main.MainScreen
import com.jetbrains.kmpapp.ui.screens.qr.QrCodeScreen
import io.github.aakira.napier.Napier

@Composable
fun Navigation(navController: NavHostController, paddingValues: PaddingValues) {
    Napier.d(tag = "AndroidWebSocket", message = "Navigation created")
//    val mainScreen = MainScreen(navController = navController, paddingValues = paddingValues)
//    val homeScreen = HomeScreen(navController = navController, paddingValues = paddingValues)
//    val qrCodeScreen = QrCodeScreen(navController = navController, paddingValues = paddingValues)

    NavHost(navController = navController, startDestination = NavigationItem.Home.route) {
        composable(NavigationItem.QRCode.route) {
            Napier.d(tag = "AndroidWebSocket", message = "QrCodeScreen open from ${navController.currentDestination?.route}")
            //if (navController.currentDestination?.route == NavigationItem.QRCode.route) {
                //qrCodeScreen
            QrCodeScreen(navController = navController, paddingValues = paddingValues)
            //}
        }
        composable(NavigationItem.Home.route) {
            Napier.d(tag = "AndroidWebSocket", message = "HomeScreen open from ${navController.currentDestination?.route}")
            //if (navController.currentDestination?.route == NavigationItem.Home.route) {
                //homeScreen
                HomeScreen(navController = navController, paddingValues = paddingValues)
            //}
        }
        composable(NavigationItem.Main.route) {
            Napier.d(tag = "AndroidWebSocket", message = "MainScreen open from ${navController.previousBackStackEntry?.destination?.route}")
            //if (navController.currentDestination?.route != NavigationItem.Main.route) {
                //mainScreen
                MainScreen(navController = navController, paddingValues = paddingValues)
            //}
        }
    }
}