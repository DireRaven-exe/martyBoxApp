package com.jetbrains.kmpapp.ui.components.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.feature.commands.CommandHandler
import com.jetbrains.kmpapp.ui.components.content.TabRowComponent
import com.jetbrains.kmpapp.ui.screens.main.views.EmptyListView
import com.jetbrains.kmpapp.ui.theme.buttonAcceptDialog
import com.jetbrains.kmpapp.ui.theme.buttonDismissDialog
import com.jetbrains.kmpapp.utils.MainUiState

@Composable
fun HomeContentView(
    uiState: MainUiState,
    commandHandler: CommandHandler,
    isFiltering: MutableState<Boolean>,
    filteredSongs: List<Song>,
    searchQuery: String,
    contentPadding: PaddingValues,
    paddingValues: PaddingValues
) {
    var expandedMenu by remember { mutableStateOf(false) }
    var expandedTabIndex by remember { mutableStateOf(-1) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val groupedSongs by remember(uiState.songs) {
        derivedStateOf { uiState.songs.groupBy { it.tab } }
    }

    val tabNames by remember(groupedSongs) {
        derivedStateOf { groupedSongs.keys.toList() }
    }

    val pagerState = rememberPagerState { tabNames.size }

    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex < tabNames.size) {
            pagerState.scrollToPage(selectedTabIndex) // Убрали анимацию
        }
    }

    // Убрали второй LaunchedEffect, передав управление ViewModel
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
        commandHandler.updateSongsForTab(tabNames[selectedTabIndex])
    }

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
    ) {
        if (searchQuery.isNotBlank()) {
            when {
                isFiltering.value -> ProgressView(contentPadding)
                filteredSongs.isEmpty() -> EmptyListView(contentPadding)
                else -> SongListHomeView(
                    songs = filteredSongs,
                    uiState = uiState,
                    commandHandler = commandHandler
                )
            }
        } else {
            if (uiState.songs.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    TabRowComponent(
                        tabNames = tabNames,
                        selectedTabIndex = selectedTabIndex,
                        onTabSelected = { index -> selectedTabIndex = index },
                        onTabLongClick = { index ->
                            expandedTabIndex = index
                            expandedMenu = true
                        }
                    )

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) { page ->
                        AnimatedVisibility(
                            visible = page == pagerState.currentPage,
                            enter = fadeIn(tween(500)),
                            exit = fadeOut(tween(500))
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                if (page != pagerState.currentPage) {
                                    // Ничего не отображать, если это не текущая папка
                                } else if (uiState.isTabLoading) {
                                    ProgressView(paddingValues)
                                } else {
                                    SongListHomeView(
                                        songs = uiState.currentSongs,
                                        uiState = uiState,
                                        commandHandler = commandHandler
                                    )
                                }
                            }
                        }
                    }
                }
            }
            else {
                EmptyListView(contentPadding)
            }
        }
    }

    PlayFolderDialog(
        showDialog = expandedMenu,
        tabName = tabNames.getOrNull(expandedTabIndex) ?: "",
        onDismiss = { expandedMenu = false },
        onConfirm = {
            if (expandedTabIndex in tabNames.indices) {
                selectedTabIndex = expandedTabIndex
                commandHandler.playTab(tab = tabNames[expandedTabIndex])
            }
            expandedMenu = false
        }
    )
}


@Composable
fun PlayFolderDialog(
    showDialog: Boolean,
    tabName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Случайное воспроизведение песен",
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis
                )
            },
            text = {
                Text(
                    text = "Вы хотите воспроизвести песни в непроизвольном порядке для ${tabName}?",
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis
                )
           },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonAcceptDialog)
                ) {
                    Text(
                        text = "Да",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonDismissDialog)

                ) {
                    Text(
                        text = "Нет",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        )
    }
}
