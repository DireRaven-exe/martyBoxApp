package com.jetbrains.kmpapp.ui.components.views.club

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.feature.commands.CommandHandler
import com.jetbrains.kmpapp.ui.components.content.TabRowComponent
import com.jetbrains.kmpapp.ui.components.views.EmptyListView
import com.jetbrains.kmpapp.ui.components.views.ProgressView
import com.jetbrains.kmpapp.utils.MainUiState

@Composable
fun ClubContentView(
    uiState: MainUiState,
    searchQuery: String,
    isFiltering: MutableState<Boolean>,
    filteredSongs: List<Song>,
    commandHandler: CommandHandler,
    contentPadding: PaddingValues,
    paddingValues: PaddingValues
) {
    val groupedSongs by remember { mutableStateOf(uiState.songs.groupBy { it.tab }) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val selectedTable by remember { mutableIntStateOf(uiState.currentTable) }

    val tabNames by remember { mutableStateOf( groupedSongs.keys.toList()) }
    val pagerState = rememberPagerState { tabNames.size }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
        commandHandler.updateSongsForTab(tabNames[selectedTabIndex])
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.TopCenter
    ) {

        if (searchQuery.isNotBlank()) {
            if (isFiltering.value) {
                ProgressView(contentPadding)
            } else {
                if (filteredSongs.isEmpty()) {
                    EmptyListView(contentPadding)
                } else {
                    SongListClubView(
                        songs = filteredSongs,
                        uiState = uiState,
                        commandHandler = commandHandler,
                        selectedTable = selectedTable
                    )
                }
            }
        } else
            if (uiState.songs.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    TabRowComponent(
                        tabNames = tabNames,
                        selectedTabIndex = selectedTabIndex,
                        onTabSelected = { index ->
                            selectedTabIndex = index
                        },
                        onTabLongClick = { }
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
                                    SongListClubView(
                                        songs = uiState.currentSongs,
                                        uiState = uiState,
                                        commandHandler = commandHandler,
                                        selectedTable = selectedTable
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                EmptyListView(contentPadding)
            }
    }
}