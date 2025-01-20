package com.jetbrains.kmpapp

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jetbrains.kmpapp.ui.screens.filter.FilterScreen
import com.jetbrains.kmpapp.ui.screens.home.HomeScreen
import com.jetbrains.kmpapp.ui.screens.main.MainScreen
import com.jetbrains.kmpapp.ui.screens.qr.QrCodeScreen
import com.jetbrains.kmpapp.ui.theme.MartinBoxAppTheme

@Composable
fun MainApplication() {
    MartinBoxAppTheme {
        val navController: NavHostController = rememberNavController()
        Scaffold() { paddingValues ->
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                NavHost(navController = navController, startDestination = "home_screen") {
                    composable("qr_code_screen") {
                        QrCodeScreen(navController = navController, paddingValues = paddingValues)
                    }
                    composable("home_screen") {
                        HomeScreen(navController = navController, paddingValues = paddingValues)
                    }
                    composable("main_screen") {
                        MainScreen(navController = navController, paddingValues = paddingValues)
                    }
                    composable("main_screen/filter_screen") {
                        FilterScreen(
                            navigator = navController,
                            paddingValues = paddingValues
                        )
                    }
                }
            }
        }
    }
}
