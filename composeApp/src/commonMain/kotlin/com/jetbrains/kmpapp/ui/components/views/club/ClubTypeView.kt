package com.jetbrains.kmpapp.ui.components.views.club

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.jetbrains.kmpapp.feature.commands.CommandHandler
import com.jetbrains.kmpapp.ui.components.content.MainTopAppBar
import com.jetbrains.kmpapp.ui.components.content.QueueTopAppBar
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.utils.MainUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.orders
import org.jetbrains.compose.resources.stringResource

@Composable
fun ClubTypeView(
    uiState: MainUiState,
    commandHandler: CommandHandler,
    paddingValues: PaddingValues,
    onNavigateToHome: () -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf(uiState.searchQuery) }


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

    val lazyListState = rememberLazyListState()

    LaunchedEffect(searchQuery) {
        delay(300) // Подождать 300 мс перед обновлением
        debouncedSearchQuery = searchQuery
    }

    var isQueueOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if(isQueueOpen) {
                QueueTopAppBar(
                    type = uiState.serverData.type,
                    title = stringResource(Res.string.orders),
                    onSheetQueueClose = { isQueueOpen = false },
                    onClearQueue = {},

                )
            } else {
                MainTopAppBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onClearSearchQuery = { searchQuery = "" },
                    onNavigateToQueue = {
                        commandHandler.updateQueue()
                        isQueueOpen = true
                    },
                    onNavigateToHome = onNavigateToHome
                )
            }
        },
        containerColor = LocalCustomColorsPalette.current.primaryBackground,
    ) { contentPadding ->
        if (isQueueOpen) {
            QueueClubContentView(
                uiState = uiState,
                commandHandler = commandHandler,
                lazyListState = lazyListState,
                contentPadding = contentPadding
            )
        }
        else {
            ClubContentView(
                uiState = uiState,
                searchQuery = searchQuery,
                isFiltering = isFiltering,
                filteredSongs = filteredSongs,
                commandHandler = commandHandler,
                contentPadding = contentPadding,
                paddingValues = paddingValues
            )
        }
    }
}
