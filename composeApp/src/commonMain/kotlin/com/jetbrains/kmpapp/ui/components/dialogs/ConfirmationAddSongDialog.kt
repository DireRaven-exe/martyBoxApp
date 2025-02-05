package com.jetbrains.kmpapp.ui.components.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jetbrains.kmpapp.ui.screens.main.views.format
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.ui.theme.buttonDisconnectDialog
import com.jetbrains.kmpapp.ui.theme.buttonReconnectDialog
import martyboxapp.composeapp.generated.resources.ConfirmAddSong
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.accept
import martyboxapp.composeapp.generated.resources.cancel
import martyboxapp.composeapp.generated.resources.pitch
import martyboxapp.composeapp.generated.resources.thinking
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun ConfirmationAddSongDialog(onDismiss: () -> Unit, addSong: (Int) -> Unit) {
    var pitch by remember { mutableStateOf(0) } // Сохраняем выбранную тональность

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.thinking),
                    contentDescription = "no connection",
                    modifier = Modifier.size(64.dp),
                    colorFilter = ColorFilter.tint(color = LocalCustomColorsPalette.current.primaryIcon)
                )
                Text(
                    text = stringResource(Res.string.ConfirmAddSong),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(stringResource(Res.string.pitch) + ": ${pitch.format(1)}")
                Slider(
                    value = pitch.toFloat(),
                    onValueChange = { newPitch ->
                        pitch = newPitch.roundToInt()
                    },
                    valueRange = -7f..7f,
                    steps = 13,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Кнопка "Cancel"
                    TextButton(
                        onClick = { onDismiss() },
                        border = BorderStroke(1.dp, buttonDisconnectDialog),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.cancel),
                            style = MaterialTheme.typography.labelLarge.copy(color = LocalCustomColorsPalette.current.primaryText),
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            addSong(pitch)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonReconnectDialog),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.accept),
                            style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
