package com.jetbrains.kmpapp.ui.screens.main.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.MainRes
import com.jetbrains.kmpapp.feature.commands.CommandHandler
import com.jetbrains.kmpapp.ui.components.SongCard
import com.jetbrains.kmpapp.utils.MainUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubTypeView(
    uiState: MainUiState,
    commandHandler: CommandHandler
    ) {
    var text by rememberSaveable { mutableStateOf(uiState.searchQuery) }
    var expanded by rememberSaveable { mutableStateOf(uiState.searchBarActive) }

    Scaffold(
        topBar = {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = SearchBarDefaults.colors().containerColor),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = text,
                        onQueryChange = { text = it },
                        onSearch = { expanded = false },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = { Text(MainRes.string.search) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                    )
                },
                shape = SearchBarDefaults.fullScreenShape,
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                if (uiState.songs.isNotEmpty()) {
                    val filteredSongs = uiState.songs.filter {
                        it.title.contains(text, ignoreCase = true) ||
                                it.artist.contains(text, ignoreCase = true)
                    }

                    if (filteredSongs.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            content = {
                                items(filteredSongs) { song ->
                                    SongCard(song,
                                        onAddClick = {
                                            commandHandler.requestMedia(song)

                                        }
                                    )
                                }
                            }
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = MainRes.string.no_songs_found
                        )
                    }
                }
            }
        }
    ) { contentPadding ->
        if (uiState.songs.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                val groupedSongs = uiState.songs.groupBy { it.tab }
                var selectedTabIndex by remember { mutableIntStateOf(0) }

                val tabNames = groupedSongs.keys.toList()
                val pagerState = rememberPagerState { tabNames.size }

                LaunchedEffect(selectedTabIndex) {
                    pagerState.animateScrollToPage(selectedTabIndex)
                }
                LaunchedEffect(pagerState.currentPage) {
                    selectedTabIndex = pagerState.currentPage
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        edgePadding = 16.dp
                    ) {
                        tabNames.forEachIndexed { index, tabName ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(tabName) }
                            )
                        }
                    }

                    val songsForSelectedTab =
                        groupedSongs[tabNames[selectedTabIndex]] ?: emptyList()

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.semantics { traversalIndex = 1f },
                                content = {
                                    items(songsForSelectedTab) { song ->
                                        SongCard(
                                            song = song,
                                            onAddClick = {
                                                commandHandler.requestMedia(song)
                                            }
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = MainRes.string.no_songs_found
                )
            }
        }
    }
}
