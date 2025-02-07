package com.jetbrains.kmpapp.utils

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.jetbrains.kmpapp.MartyBoxApp

object NotificationPermissionDialog {

    private var isDialogVisible = mutableStateOf(false)

    fun show() {
        isDialogVisible.value = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun NotificationDialog() {
        if (isDialogVisible.value) {
            AlertDialog(
                onDismissRequest = { isDialogVisible.value = false },
                title = { Text("Разрешение на уведомления") },
                text = { Text("Для корректной работы приложения нужно разрешение на уведомления. Перейти в настройки?") },
                confirmButton = {
                    Button(onClick = {
                        isDialogVisible.value = false
                        openNotificationSettings()
                    }) {
                        Text("Перейти")
                    }
                },
                dismissButton = {
                    Button(onClick = { isDialogVisible.value = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openNotificationSettings() {
        val context = MartyBoxApp.getAppContext()
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}