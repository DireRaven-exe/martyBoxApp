package com.jetbrains.kmpapp.ui.components.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.ui.theme.buttonDismissDialog
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.noConnectionToServer
import martyboxapp.composeapp.generated.resources.no_connection
import martyboxapp.composeapp.generated.resources.to_home
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ServerDisconnectedDialog(onDisconnect: () -> Unit, onReconnect: () -> Unit) {
    Dialog(onDismissRequest = onDisconnect) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.no_connection),
                    contentDescription = "no connection",
                    modifier = Modifier.size(64.dp),
                    colorFilter = ColorFilter.tint(color = LocalCustomColorsPalette.current.primaryIcon)
                )
                Text(
                    text = stringResource(Res.string.noConnectionToServer),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = onDisconnect,
                        border = BorderStroke(1.dp, buttonDismissDialog),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.to_home),
                            style = MaterialTheme.typography.bodySmall.copy(color = LocalCustomColorsPalette.current.primaryText)
                        )
                    }

//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    Button(
//                        onClick = onReconnect,
//                        colors = ButtonDefaults.buttonColors(containerColor = buttonReconnectDialog),
//                        modifier = Modifier
//                            .weight(1f)
//                            .height(48.dp)
//                    ) {
//                        Text(
//                            text = stringResource(Res.string.reconnect),
//                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
//                            maxLines = 1,
//                            overflow = TextOverflow.Ellipsis
//                        )
//                    }
                }
            }
        }
    }
}
