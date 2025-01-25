package com.jetbrains.kmpapp.ui.screens.queue

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.feature.dragdropcolumncompose.DragNDropItemsList
import com.jetbrains.kmpapp.ui.screens.main.views.EmptyListView
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.queue
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
    navController: NavHostController,
    queueViewModel: QueueViewModel = koinInject(),
    paddingValues: PaddingValues
) {
    val uiState = queueViewModel.uiState.collectAsState().value
    val songs = remember { mutableStateListOf<Song>().apply { addAll(uiState.songs) } }
    val currentSong = uiState.currentSong

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.queue)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (songs.isNotEmpty()) {
                val isPlaying: (Song) -> Boolean = { song ->
                    currentSong?.let {
                        song.id == it.id &&
                                song.artist == it.artist &&
                                song.title == it.title &&
                                uiState.isPlaying
                    } ?: false
                }

                val isCurrentSong: (Song) -> Boolean = { song ->
                    currentSong?.let { it == song } ?: false
                }

                DragNDropItemsList(
                    items = songs,
                    isPlaying = false,//isPlaying(currentSong),
                    isCurrentSong = false,//isCurrentSong(currentSong),
                    onPlayClick = { song -> /* Действие воспроизведения */ },
                    onRemoveClick = { song -> songs.remove(song) },
                    onEditPositionClick = { /* Логика начала перетаскивания */ },
                    onItemClicked = { song -> /* Действие клика по песне */ }
                )
            } else {
                EmptyListView(contentPadding)
            }
        }
    }
}

fun isPlaying(song: Song, currentSong: Song, isPlaying: Boolean): Boolean {
    return song.id == currentSong.id &&
            song.artist == currentSong.artist &&
            song.title == currentSong.title &&
            isPlaying
}