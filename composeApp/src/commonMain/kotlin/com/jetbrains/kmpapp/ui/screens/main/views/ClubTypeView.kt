package com.jetbrains.kmpapp.ui.screens.main.views

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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jetbrains.kmpapp.feature.commands.MainCommandHandler
import com.jetbrains.kmpapp.ui.components.content.MainTopAppBar
import com.jetbrains.kmpapp.ui.components.content.SongListClubView
import com.jetbrains.kmpapp.ui.components.content.TabRowComponent
import com.jetbrains.kmpapp.ui.components.views.ProgressView
import com.jetbrains.kmpapp.ui.screens.main.MainViewModel
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.utils.MainUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun ClubTypeView(
    uiState: MainUiState,
    mainViewModel: MainViewModel,
    mainCommandHandler: MainCommandHandler,
    paddingValues: PaddingValues,
    navigationToQueue: () -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf(uiState.searchQuery) }
    val groupedSongs by remember { mutableStateOf(uiState.songs.groupBy { it.tab }) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val selectedTable by remember { mutableIntStateOf(uiState.currentTable) }

    val tabNames by remember { mutableStateOf( groupedSongs.keys.toList()) }
    val pagerState = rememberPagerState { tabNames.size }

    var debouncedSearchQuery by remember { mutableStateOf(searchQuery) }

    val isFiltering = remember { mutableStateOf(false) }

    val filteredSongs by produceState(initialValue = emptyList(), debouncedSearchQuery, uiState.songs) {
        isFiltering.value = true
        value = withContext(Dispatchers.Default) {
            if (debouncedSearchQuery.isBlank()) uiState.songs
            else uiState.songs.filter { song ->
                val titleWords = song.title.split(" ").map { it.lowercase() }
                val artistWords = song.artist.split(" ").map { it.lowercase() }
                val queryWords = debouncedSearchQuery.split(" ").map { it.lowercase() }
                queryWords.all { queryWord ->
                    titleWords.any { it.contains(queryWord) } || artistWords.any { it.contains(queryWord) }
                }
            }
        }
        isFiltering.value = false
    }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
        delay(1000)
        mainViewModel.updateSongsForTab(tabNames[selectedTabIndex])
    }

    LaunchedEffect(searchQuery) {
        delay(300) // Подождать 300 мс перед обновлением
        debouncedSearchQuery = searchQuery
    }

    Scaffold(
        topBar = {
            MainTopAppBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onClearSearchQuery = { searchQuery = "" },
                onNavigateToQueue =  { navigationToQueue() }
            )
        },
        containerColor = LocalCustomColorsPalette.current.primaryBackground,
    ) { contentPadding ->
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
                            mainCommandHandler = mainCommandHandler,
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
                                            mainCommandHandler = mainCommandHandler,
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
}
