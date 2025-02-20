package com.jetbrains.kmpapp.ui.components.views.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.ui.components.content.SongCard
import com.jetbrains.kmpapp.ui.screens.demo.DemoViewModel
import com.jetbrains.kmpapp.utils.DemoUiState

@Composable
fun SongListDemoView(
    songs: List<Song>,
    uiState: DemoUiState,
    viewModel: DemoViewModel
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
            items(songs) { song ->
                SongCard(
                    song = song,
                    isPlaying = song.id == uiState.currentSong?.id
                            && song.artist == uiState.currentSong.artist
                            && song.title == uiState.currentSong.title
                            && uiState.isPlaying,
                    onPlayClick = { viewModel.play(song) },
                    onAddClick = { viewModel.addSongToQueue(song) },
                    isCurrentSong = song == uiState.currentSong,
                )
            }
        }
    )
}
