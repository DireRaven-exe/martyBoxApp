package com.jetbrains.kmpapp.ui.components.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.pitch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun ContextMenu(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onSetPitch: (Int) -> Unit,
) {
    var showPitchDialog by remember { mutableStateOf(false) } // Состояние для отображения диалога с тональностью
    var pitch by remember { mutableStateOf(0) } // Состояние для хранения выбранной тональности

    // Основной диалог с действиями
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Выберите действие", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                // Кнопка "Указать тональность"
                Button(
                    onClick = { showPitchDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Указать тональность",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Указать тональность",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Кнопка "Удалить"
                Button(
                    onClick = {
                        onDelete()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Удалить",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )

    // Диалог для выбора тональности
    if (showPitchDialog) {
        AlertDialog(
            onDismissRequest = { showPitchDialog = false },
            title = { Text("Выберите тональность", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column {
                    // Слайдер для выбора тональности
                    CustomSliderWithButtons(
                        label = stringResource(Res.string.pitch),
                        value = pitch.toFloat(),
                        valueRange = -7f..7f,
                        steps = 13,
                        stepSize = 1f,
                        format = { it.roundToInt().toString() },
                        onValueChange = {
                            pitch = it.roundToInt()
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetPitch(pitch)
                        showPitchDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Применить", style = MaterialTheme.typography.bodyLarge)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showPitchDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text("Отмена", style = MaterialTheme.typography.bodyLarge)
                }
            }
        )
    }
}