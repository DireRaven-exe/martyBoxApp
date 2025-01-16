package com.jetbrains.kmpapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.domain.models.Song
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.pause
import org.jetbrains.compose.resources.painterResource

@Composable
fun SongCard(song: Song, isPlaying: Boolean, onPlayClick: (Song) -> Unit, onAddClick: (Song) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(5.dp)
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = song.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, fontWeight = FontWeight.Bold)
                Text(text = song.artist, style = MaterialTheme.typography.titleSmall, maxLines = 1)
            }

            IconButton(
                onClick = { onPlayClick(song) },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                if (isPlaying) {
                    Icon(
                        painter = painterResource(Res.drawable.pause),
                        contentDescription = "Pause",
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play"
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
fun SongCard(song: Song, onAddClick: (Song) -> Unit) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(5.dp)
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = song.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, fontWeight = FontWeight.Bold)
                Text(text = song.artist, style = MaterialTheme.typography.titleSmall, maxLines = 1)
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
        ConfirmationAddSongDialog(onDismiss = { showDialog = false }, addSong = { onAddClick(song) })
    }
}
