package com.jetbrains.kmpapp.ui.components.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.domain.models.SongInQueue
import com.jetbrains.kmpapp.ui.components.dialogs.ConfirmationAddSongDialog
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.pause
import org.jetbrains.compose.resources.painterResource

@Composable
fun SongCard(
    song: Song,
    isPlaying: Boolean,
    isCurrentSong: Boolean,
    onPlayClick: (Song) -> Unit,
    onAddClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Transparent)
            .border(
                width = 2.dp,
                color = if (isCurrentSong) LocalCustomColorsPalette.current.selectedIcon else Color.Transparent,
                shape = RoundedCornerShape(15.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrentSong) LocalCustomColorsPalette.current.selectedIcon else LocalCustomColorsPalette.current.primaryText
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    color = LocalCustomColorsPalette.current.secondaryText
                )
            }

            IconButton(
                onClick = { onPlayClick(song) },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                if (isPlaying) {
                    Icon(
                        painter = painterResource(Res.drawable.pause),
                        contentDescription = "Pause",
                        modifier = Modifier.size(26.dp)
                    )

                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            IconButton(
                onClick = { onAddClick(song) },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}

@Composable
fun SongCard(
    song: SongInQueue,
    isPlaying: Boolean,
    isCurrentSong: Boolean,
    onPlayClick: (SongInQueue) -> Unit,
    onRemoveSong: (SongInQueue) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Transparent)
            .border(
                width = 2.dp,
                color = if (isCurrentSong) LocalCustomColorsPalette.current.selectedIcon else Color.Transparent,
                shape = RoundedCornerShape(15.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrentSong) LocalCustomColorsPalette.current.selectedIcon else LocalCustomColorsPalette.current.primaryText
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    color = LocalCustomColorsPalette.current.secondaryText
                )
            }

            IconButton(
                onClick = { onPlayClick(song) },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                if (isPlaying) {
                    Icon(
                        painter = painterResource(Res.drawable.pause),
                        contentDescription = "Pause",
                        modifier = Modifier.size(26.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            IconButton(
                onClick = { onRemoveSong(song) },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove song",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun SongCard(
    song: SongInQueue,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Transparent)
            .border(
                width = 2.dp,
                color = Color.Transparent,
                shape = RoundedCornerShape(15.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    color = LocalCustomColorsPalette.current.primaryText
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    color = LocalCustomColorsPalette.current.secondaryText
                )
            }
        }
    }
}

@Composable
fun SongCard(song: Song, onAddClick: (Song, Int) -> Unit, isCurrentSong: Boolean) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Transparent)
            .border(
                width = 2.dp,
                color = if (isCurrentSong) LocalCustomColorsPalette.current.selectedIcon else Color.Transparent,
                shape = RoundedCornerShape(15.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrentSong) LocalCustomColorsPalette.current.selectedIcon else LocalCustomColorsPalette.current.primaryText
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    color = LocalCustomColorsPalette.current.secondaryText
                )
            }

            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }

    if (showDialog) {
        ConfirmationAddSongDialog(
            onDismiss = { showDialog = false },
            addSong = { pitch ->
                onAddClick(song, pitch)
            }
        )
    }
}
