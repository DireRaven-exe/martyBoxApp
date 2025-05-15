package com.jetbrains.kmpapp.ui.components.content

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.ui.components.views.CustomSearchView
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearchQuery: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 16.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomSearchView(
                    search = searchQuery,
                    onValueChange = onSearchQueryChange,
                    onClearSearchQuery = onClearSearchQuery,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .wrapContentHeight()
                )



            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateToHome) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Home",
                    modifier = Modifier.size(36.dp),
                    tint = LocalCustomColorsPalette.current.primaryIcon
                )
            }
        },
        actions = {
            IconButton(onClick = onNavigateToQueue) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Queue",
                    modifier = Modifier.size(36.dp),
                    tint = LocalCustomColorsPalette.current.primaryIcon
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LocalCustomColorsPalette.current.primaryBackground
        )
    )
}