package com.jetbrains.kmpapp.feature.backhandler

import androidx.compose.runtime.Composable

@Composable
expect fun OnBackPressedHandler(isServerConnected: Boolean, isLoading: Boolean,
                                onBackFromLoading: () -> Unit,
                                onBackFromServerConnected: () -> Unit,
                                onBackDefault: () -> Unit)