package com.jetbrains.kmpapp.ui.components.views.club

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.domain.models.SongInQueue
import com.jetbrains.kmpapp.domain.models.generateUniqueKey
import com.jetbrains.kmpapp.feature.commands.CommandHandler
import com.jetbrains.kmpapp.ui.components.content.SongCard
import com.jetbrains.kmpapp.ui.components.views.EmptyListView
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.utils.MainUiState

@Composable
fun QueueClubContentView(
    uiState: MainUiState,
    commandHandler: CommandHandler,
    lazyListState: LazyListState,
    contentPadding: PaddingValues,
) {
    // Используем SnapshotStateList для отслеживания изменений
    val currentPlaylist = remember { mutableStateListOf<SongInQueue>() }
    val keyMap = remember { mutableStateMapOf<Int, String>() }

    // Обновляем список только при изменении данных
    LaunchedEffect(uiState.currentPlaylist) {
        if (currentPlaylist != uiState.currentPlaylist) {
            currentPlaylist.clear()
            currentPlaylist.addAll(uiState.currentPlaylist.map { song ->
                val existingKey = keyMap[song.id]
                val key = existingKey ?: generateUniqueKey(song.id).also { keyMap[song.id] = it }
                song.copy(key = key)
            })
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.TopCenter
    ) {
        if (currentPlaylist.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                itemsIndexed(
                    currentPlaylist,
                    //key = { _, item -> item.key }
                ) { index, item ->
                    Card(
                        onClick = {
                            // Обработка клика
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = LocalCustomColorsPalette.current.primaryBackground
                        ),
                        elevation = CardDefaults.cardElevation(0.dp),
                        modifier = Modifier
                    ) {
                        SongCard(song = item)
                    }
                }
            }
        } else {
            EmptyListView(contentPadding)
        }
    }
}
