package com.jetbrains.kmpapp.ui.components.content

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueTopAppBar(
    title: String,
    onSheetQueueClose: () -> Unit,
    onClearQueue: () -> Unit,
    type: String
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = { onSheetQueueClose() } ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Queue",
                    modifier = Modifier.size(36.dp),
                    tint = LocalCustomColorsPalette.current.primaryIcon
                )
            }
        },
        actions = {
            if (type == Constants.TYPE_HOME) {
                IconButton(onClick = { onClearQueue() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Queue",
                        modifier = Modifier.size(36.dp),
                        tint = LocalCustomColorsPalette.current.primaryIcon
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LocalCustomColorsPalette.current.primaryBackground
        )
    )
}