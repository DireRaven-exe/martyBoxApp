package com.jetbrains.kmpapp.ui.screens.main.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
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
import com.jetbrains.kmpapp.feature.commands.CommandHandler
import com.jetbrains.kmpapp.ui.components.CustomSearchView
import com.jetbrains.kmpapp.ui.components.SongCard
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.utils.MainUiState
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.filter_icon
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubTypeView(
    uiState: MainUiState,
    commandHandler: CommandHandler,
    filtersState: Map<String, List<String>>,
    paddingValues: PaddingValues,
    navigateToFilter: () -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf(uiState.searchQuery) }
    var expanded by rememberSaveable { mutableStateOf(uiState.searchBarActive) }

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

    val filteredSongs = remember(searchQuery, uiState.songs, uiState.artistSearchActive, uiState.titleSearchActive) {
        if (searchQuery.isBlank()) {
            uiState.songs
        } else {
            uiState.songs.filter { song ->
                val titleWords = song.title.split(" ").map { it.lowercase() }
                val artistWords = song.artist.split(" ").map { it.lowercase() }
                val queryWords = searchQuery.split(" ").map { it.lowercase() }

                queryWords.all { queryWord ->
                    when {
                        uiState.artistSearchActive && uiState.titleSearchActive -> {
                            titleWords.any { it.contains(queryWord) } || artistWords.any { it.contains(queryWord) }
                        }
                        uiState.artistSearchActive -> {
                            artistWords.any { it.contains(queryWord) }
                        }
                        uiState.titleSearchActive -> {
                            titleWords.any { it.contains(queryWord) }
                        }
                        else -> false // Если оба переключателя выключены (не должно быть такого, но на всякий случай).
                    }
                }
            }
        }
    }

    val finalFilteredSongs = remember(filteredSongs, filtersState) {
        filteredSongs?.filter { song ->
            filtersState.all { (key, values) ->
                when (key) {
                    "artist" -> values.any { song.artist.contains(it, ignoreCase = true) }
                    else -> true
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomSearchView(
                    search = searchQuery,
                    onValueChange = { searchQuery = it },
                    onClearSearchQuery = { searchQuery = "" },
                    modifier = Modifier
                        .height(50.dp)
                        .weight(0.9f)
                        .padding(end = 16.dp)
                )
                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = {
                        commandHandler.setTitleSearchActive(uiState.titleSearchActive)
                        commandHandler.setArtistSearchActive(uiState.artistSearchActive)
                        navigateToFilter()
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.filter_icon),
                        modifier = Modifier.size(24.dp),
                        tint = LocalCustomColorsPalette.current.primaryIcon,
                        contentDescription = "Фильтры"
                    )
                }
            }
        },
        containerColor = LocalCustomColorsPalette.current.primaryBackground,
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.TopCenter
        ) {

            if (searchQuery.isNotBlank() || filtersState.isNotEmpty()) {
                if (finalFilteredSongs!!.isEmpty()) {
                    EmptyListView(contentPadding)
                } else
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        content = {
                            items(finalFilteredSongs!!) { song ->
                                SongCard(
                                    song = song,
                                    isCurrentSong = song == uiState.currentSong,
                                    onAddClick = {
                                        commandHandler.requestMedia(song)
                                    }
                                )
                            }
                        }
                    )
            } else
                if (uiState.songs.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
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
                                .weight(1f),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier.semantics { traversalIndex = 1f },
                                    contentPadding = PaddingValues(
                                        top = 10.dp,
                                        bottom = 10.dp,
                                        start = 2.dp,
                                        end = 2.dp
                                    ),
                                    content = {
                                        items(songsForSelectedTab) { song ->
                                            SongCard(
                                                song = song,
                                                isCurrentSong = song == uiState.currentSong,
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

                } else {
                    EmptyListView(contentPadding)
                }
        }
    }
}
