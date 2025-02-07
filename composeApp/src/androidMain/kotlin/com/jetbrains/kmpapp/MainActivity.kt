package com.jetbrains.kmpapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.jetbrains.kmpapp.utils.NotificationPermissionDialog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            // Remove when https://issuetracker.google.com/issues/364713509 is fixed
//            LaunchedEffect(isSystemInDarkTheme()) {
//                enableEdgeToEdge()
//            }
            Napier.base(DebugAntilog())
            NotificationPermissionDialog.NotificationDialog()
            MainApplication()
        }
    }
}
