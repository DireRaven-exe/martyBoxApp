package com.jetbrains.kmpapp.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector?
) {

    object Home : NavigationItem("/home_screen", "Home", null)
    object QRCode : NavigationItem("/qr_code_screen", "QR", null)
    object Main : NavigationItem("/main_screen", "Main", null)

    object Queue : NavigationItem("/queue_screen", "Queue", null)
}