package com.jetbrains.kmpapp.ui.components.views

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.domain.models.SongInQueue
import com.jetbrains.kmpapp.feature.commands.CommandHandler
import com.jetbrains.kmpapp.feature.vibration.HapticFeedbackType
import com.jetbrains.kmpapp.feature.vibration.provideHapticFeedback
import com.jetbrains.kmpapp.ui.components.content.ContextMenu
import com.jetbrains.kmpapp.ui.components.content.SongCard
import com.jetbrains.kmpapp.ui.screens.main.views.EmptyListView
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.utils.MainUiState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun QueueHomeContentView(
    uiState: MainUiState,
    commandHandler: CommandHandler,
    paddingValues: PaddingValues,
    lazyListState: LazyListState,
    contentPadding: PaddingValues,
) {

    val currentPlaylist = remember(uiState.currentPlaylist) { uiState.currentPlaylist }
    val hapticFeedback = provideHapticFeedback()

    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        commandHandler.moveOn(from.index, to.index)
        commandHandler.moveSongInQueue(from.index, to.index)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.Heavy)
    }

    var selectedSong by remember { mutableStateOf<SongInQueue?>(null) }
    var selectedIndex by remember { mutableStateOf<Int>(-1) }
    var showContextMenu by remember { mutableStateOf(false) }

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
                            onClick = {
                                selectedSong = item
                                selectedIndex = index
                                showContextMenu = true
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDragging) {
                                    LocalCustomColorsPalette.current.cardCurrentSongBackground
                                } else {
                                    LocalCustomColorsPalette.current.primaryBackground
                                }
                            ),
                            elevation = CardDefaults.cardElevation(0.dp),
                            modifier = Modifier,
                            interactionSource = interactionSource,
                        ) {
                            SongCard(
                                song = item,
                                isPlaying = false,
                                isCurrentSong = false,
                                onPlayClick = {
                                    commandHandler.playSoundInPlaylist(index, song = item)
                                },
                                modifier = Modifier.draggableHandle(
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
                            )

                        }
                    }
                }
            }
        } else {
            EmptyListView(paddingValues)
        }
    }

    if (showContextMenu && selectedSong != null) {
        ContextMenu(
            onDismiss = { showContextMenu = false },
            onDelete = {
                commandHandler.removeSong(selectedSong!!)
                commandHandler.removeSoundFromPlaylist(currentPlaylist.indexOf(selectedSong))
                showContextMenu = false
            },
            onSetPitch = { newPitch ->
                commandHandler.setPitchToFile(selectedIndex, newPitch)
                showContextMenu = false
            },
        )
    }

}