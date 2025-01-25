package com.jetbrains.kmpapp.feature.dragdropcolumncompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.ui.components.SongCard

@Composable
fun DragNDropItemsList(
    items: SnapshotStateList<Song>,
    isPlaying: Boolean,
    isCurrentSong: Boolean,
    onPlayClick: (Song) -> Unit,
    onRemoveClick: (Song) -> Unit,
    onEditPositionClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onItemClicked: (Song) -> Unit = {}
) {
    fun swapItems(from: Int, to: Int) {
        val fromItem = items[from]
        val toItem = items[to]
        items[from] = toItem
        items[to] = fromItem
    }

    DragDropColumn(
        items = items,
        onSwap = ::swapItems
    ) { song ->
        SongCard(
            song = song,
            isPlaying = isPlaying,
            isCurrentSong = isCurrentSong,
            onPlayClick = { onPlayClick(song) },
            onRemoveClick = { onRemoveClick(song) },
            onEditPositionClick = { onEditPositionClick(items.indexOf(song)) },
            onItemClicked = { onItemClicked(song) }
        )
    }
}