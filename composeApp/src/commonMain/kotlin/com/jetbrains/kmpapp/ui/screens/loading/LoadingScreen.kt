package com.jetbrains.kmpapp.ui.screens.loading

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.app_name
import martyboxapp.composeapp.generated.resources.cancel
import martyboxapp.composeapp.generated.resources.martinlogo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoadingScreen(onCancelClick: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) { contentPadding ->
        Box(modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()) {

                IconButton(
                    onClick = onCancelClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.cancel),
                        tint = LocalCustomColorsPalette.current.primaryText
                    )
                }

                Image(
                    painter = painterResource(Res.drawable.martinlogo),
                    contentDescription = stringResource(Res.string.app_name),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Fit,
                )
        }
    }
}