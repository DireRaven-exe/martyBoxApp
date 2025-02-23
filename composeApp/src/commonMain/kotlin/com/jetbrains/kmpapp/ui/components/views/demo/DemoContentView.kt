package com.jetbrains.kmpapp.ui.components.views.demo

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.ui.components.content.TabRowComponent
import com.jetbrains.kmpapp.ui.components.views.EmptyListView
import com.jetbrains.kmpapp.ui.components.views.ProgressView
import com.jetbrains.kmpapp.ui.screens.demo.DemoViewModel
import com.jetbrains.kmpapp.utils.DemoUiState

@Composable
fun DemoContentView(
    uiState: DemoUiState,
    isFiltering: MutableState<Boolean>,
    filteredSongs: List<Song>,
    searchQuery: String,
    contentPadding: PaddingValues,
    paddingValues: PaddingValues,
    viewModel: DemoViewModel
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
        if (tabNames.isNotEmpty()) {
            viewModel.updateSongsForTab(tabNames[selectedTabIndex])
        }
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
                else -> SongListDemoView(
                    songs = filteredSongs,
                    uiState = uiState,
                    viewModel = viewModel
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
                                    SongListDemoView(
                                        songs = uiState.currentSongs,
                                        uiState = uiState,
                                        viewModel = viewModel
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
}