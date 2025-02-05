package com.jetbrains.kmpapp.ui.components.content

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.feature.commands.MainCommandHandler
import com.jetbrains.kmpapp.utils.MainUiState

@Composable
fun SongListClubView(
    songs: List<Song>,
    selectedTable: Int,
    uiState: MainUiState,
    mainCommandHandler: MainCommandHandler
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        content = {
            items(songs) { song ->
                SongCard(
                    song = song,
                    isCurrentSong = song == uiState.currentSong,
                    onAddClick = { song, pitch ->
                        mainCommandHandler.requestMedia(
                            song,
                            selectedTable,
                            pitch
                        )
                    }
                )
            }
        }
    )
}