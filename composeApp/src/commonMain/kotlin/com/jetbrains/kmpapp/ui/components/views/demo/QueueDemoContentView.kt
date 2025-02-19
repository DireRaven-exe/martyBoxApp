package com.jetbrains.kmpapp.ui.components.views.demo

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.feature.vibration.HapticFeedbackType
import com.jetbrains.kmpapp.feature.vibration.provideHapticFeedback
import com.jetbrains.kmpapp.ui.components.content.SongCard
import com.jetbrains.kmpapp.ui.components.views.EmptyListView
import com.jetbrains.kmpapp.ui.screens.demo.DemoViewModel
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.utils.DemoUiState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun QueueDemoContentView(
    uiState: DemoUiState,
    paddingValues: PaddingValues,
    lazyListState: LazyListState,
    contentPadding: PaddingValues,
    viewModel: DemoViewModel
) {

    val keyMap = remember { mutableStateMapOf<Int, String>() }
//    val currentPlaylist = remember(uiState.currentPlaylist) {
//        uiState.currentPlaylist.map { song ->
//            val existingKey = keyMap[song.id]
//            val key = existingKey ?: generateUniqueKey(song.id).also { keyMap[song.id] = it }
//            song.copy(key = key)
//        }
//    }

    val currentPlaylist = remember(uiState.currentPlaylist) { uiState.currentPlaylist }
    val hapticFeedback = provideHapticFeedback()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        viewModel.moveSongInQueue(from.index, to.index)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.Heavy)
    }

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
    ) {
        if (uiState.currentPlaylist.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = lazyListState,
            ) {
                itemsIndexed(
                    currentPlaylist,
                    key = { _, item -> item.key }) { index, item ->
                    ReorderableItem(
                        reorderableLazyListState,
                        key = item.key
                    ) { isDragging ->
                        val interactionSource = remember { MutableInteractionSource() }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDragging) {
                                    LocalCustomColorsPalette.current.cardCurrentSongBackground
                                } else {
                                    LocalCustomColorsPalette.current.primaryBackground
                                }
                            ),
                            elevation = CardDefaults.cardElevation(0.dp),
                            modifier = Modifier
                                .longPressDraggableHandle(
                                    onDragStarted = {
                                        hapticFeedback.performHapticFeedback(
                                            HapticFeedbackType.Heavy
                                        )
                                    },
                                    onDragStopped = {
                                        hapticFeedback.performHapticFeedback(
                                            HapticFeedbackType.Heavy
                                        )
                                    },
                                    interactionSource = interactionSource
                                ),
                        ) {
                            SongCard(
                                song = item,
                                isPlaying = false,
                                isCurrentSong = false,
                                onPlayClick = {
                                    viewModel.playSoundInPlaylist(index, song = item)
                                },
                                onRemoveSong = { song ->
                                    viewModel.removeSong(song)
                                },
                            )

                        }
                    }
                }
            }
        } else {
            EmptyListView(paddingValues)
        }
    }
}