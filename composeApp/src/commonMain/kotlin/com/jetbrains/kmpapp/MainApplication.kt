package com.jetbrains.kmpapp

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jetbrains.kmpapp.ui.navigation.Navigation
import com.jetbrains.kmpapp.ui.theme.MartinBoxAppTheme

@Composable
fun MainApplication() {
    MartinBoxAppTheme {
        val navController: NavHostController = rememberNavController()
        Scaffold() { paddingValues ->
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                Navigation(navController = navController, paddingValues = paddingValues)
            }
        }
    }
}
