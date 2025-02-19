package com.jetbrains.kmpapp.ui.screens.demo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.jetbrains.kmpapp.ui.components.views.demo.DemoTypeView
import com.jetbrains.kmpapp.ui.navigation.NavigationItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DemoScreen(
    navController: NavHostController,
    viewModel: DemoViewModel = koinViewModel<DemoViewModel>(),
    paddingValues: PaddingValues
) {
    val uiState = viewModel.uiState.collectAsState().value

    DemoTypeView(
        uiState = uiState,
        viewModel = viewModel,
        paddingValues = paddingValues,
        onExitDemoMode = {
            navController.navigate(NavigationItem.Home.route) {
                popUpTo(NavigationItem.Home.route) { inclusive = true }
            }
        }
    )
}