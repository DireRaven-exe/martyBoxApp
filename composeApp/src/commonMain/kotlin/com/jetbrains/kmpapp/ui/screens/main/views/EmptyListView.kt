package com.jetbrains.kmpapp.ui.screens.main.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.no_songs_found
import martyboxapp.composeapp.generated.resources.sad
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyListView(contentPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(Res.drawable.sad),
                contentDescription = "Sad face",
                modifier = Modifier.size(64.dp),
                colorFilter = ColorFilter.tint(color = LocalCustomColorsPalette.current.primaryIcon)
            )
            Text(
                text = stringResource(Res.string.no_songs_found),
                color = LocalCustomColorsPalette.current.primaryText

            )
        }
    }
}