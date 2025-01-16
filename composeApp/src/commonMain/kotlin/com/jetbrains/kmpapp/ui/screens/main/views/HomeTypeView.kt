package com.jetbrains.kmpapp.ui.screens.main.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.feature.commands.CommandHandler
import com.jetbrains.kmpapp.ui.components.SongCard
import com.jetbrains.kmpapp.utils.MainUiState
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.artist
import martyboxapp.composeapp.generated.resources.autoFullScreen
import martyboxapp.composeapp.generated.resources.moveBottomSheet
import martyboxapp.composeapp.generated.resources.music_plus
import martyboxapp.composeapp.generated.resources.next
import martyboxapp.composeapp.generated.resources.no_songs_found
import martyboxapp.composeapp.generated.resources.pause
import martyboxapp.composeapp.generated.resources.pitch
import martyboxapp.composeapp.generated.resources.search
import martyboxapp.composeapp.generated.resources.singingAssessment
import martyboxapp.composeapp.generated.resources.soundInPause
import martyboxapp.composeapp.generated.resources.tempo
import martyboxapp.composeapp.generated.resources.title
import martyboxapp.composeapp.generated.resources.volume
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.pow
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTypeView(
    commandHandler: CommandHandler,
    uiState: MainUiState
) {
    var text by rememberSaveable { mutableStateOf(uiState.searchQuery) }
    var expanded by rememberSaveable { mutableStateOf(uiState.searchBarActive) }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val currentSong = uiState.currentSong

    val isSheetExpanded by derivedStateOf {
        scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 128.dp,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(128.dp), contentAlignment = Alignment.TopCenter) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = currentSong?.title ?: stringResource(Res.string.title),
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = currentSong?.artist ?: stringResource(Res.string.artist),
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1
                            )


                        }
                        IconButton(onClick = {
                            if (!uiState.isPlaying) commandHandler.playAfterPause()
                            else commandHandler.pause()
                        }
                        ) {
                            if (!uiState.isPlaying) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    modifier = Modifier.size(48.dp)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(Res.drawable.pause),
                                    contentDescription = "Pause",
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        IconButton(onClick = { commandHandler.next() }) {
                            Icon(
                                painter = painterResource(Res.drawable.next),
                                contentDescription = "Next",
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        IconButton(
                            enabled = uiState.hasPlus,
                            onClick = { commandHandler.switchPlusMinus() }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.music_plus),
                                contentDescription = "Next",
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    }
                }

                Text(text = stringResource(Res.string.volume) + ": ${(uiState.volume * 100).roundToInt()}%")
                Slider(
                    value = uiState.volume,
                    onValueChange = {
                        commandHandler.volume(it)
                        commandHandler.updateVolume(it)
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                )

                Text(text = stringResource(Res.string.tempo) + ": ${uiState.tempo.format(2)}x")
                Slider(
                    value = uiState.tempo,
                    onValueChange = {
                        commandHandler.changeTempo(it)
                        commandHandler.updateTempo(it)
                    },
                    valueRange = 0.5f..1.5f,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                )

                Text(stringResource(Res.string.pitch) + ": ${uiState.pitch.format(2)}")
                Slider(
                    value = uiState.pitch.toFloat(),
                    onValueChange = {
                        commandHandler.changePitch(it.roundToInt())
                        commandHandler.updatePitch(it.roundToInt())
                    },
                    valueRange = -7f..7f,
                    steps = 13,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            Column(modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Switch(
                        checked = uiState.autoFullScreen,
                        onCheckedChange = {
                            commandHandler.changeAutoFullScreen(it)
                            commandHandler.updateAutoFullScreen(it)
                        }
                    )
                    Text(
                        text = stringResource(Res.string.autoFullScreen),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                }

                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Switch(
                        checked = uiState.singingAssessment,
                        onCheckedChange = {
                            commandHandler.changeSingingAssessment(it)
                            commandHandler.updateSingingAssessment(it)

                        }
                    )
                    Text(
                        text = stringResource(Res.string.singingAssessment),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                }

                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Switch(
                        checked = uiState.soundInPause,
                        onCheckedChange = {
                            commandHandler.changeSoundInPause(it)
                            commandHandler.updateSoundInPause(it)

                        }
                    )
                    Text(
                        text = stringResource(Res.string.soundInPause),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                }
            }
        },
        sheetShape = RoundedCornerShape(0.dp),
        sheetDragHandle = {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSheetExpanded) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown, // Стрелка вниз
                        contentDescription = stringResource(Res.string.moveBottomSheet),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp, // Стрелка вверх
                        contentDescription = stringResource(Res.string.moveBottomSheet),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = stringResource(Res.string.moveBottomSheet),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        topBar = {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = SearchBarDefaults.colors().containerColor),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = text,
                        onQueryChange = { text = it },
                        onSearch = { expanded = false },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = { Text(stringResource(Res.string.search)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    )
                },
                shape = SearchBarDefaults.fullScreenShape,
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                if (uiState.songs.isNotEmpty()) {
                    val filteredSongs = uiState.songs.filter {
                        it.title.contains(text, ignoreCase = true) ||
                                it.artist.contains(text, ignoreCase = true)
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        content = {
                            items(filteredSongs) { song ->
                                SongCard(
                                    song = song,
                                    isPlaying = song.id == uiState.currentSong?.id
                                            && song.artist == uiState.currentSong?.artist
                                            && song.title == uiState.currentSong?.title
                                            && uiState.isPlaying,
                                    onPlayClick = { commandHandler.play(song) },
                                    onAddClick = { commandHandler.appendMedia(song) }
                                )
                            }
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.no_songs_found)
                        )
                    }
                }
            }
        }
    ) { contentPadding ->
        if (uiState.songs.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                val groupedSongs = uiState.songs.groupBy { it.tab }
                var selectedTabIndex by remember { mutableIntStateOf(0) }

                val tabNames = groupedSongs.keys.toList()
                val pagerState = rememberPagerState { tabNames.size }

                LaunchedEffect(selectedTabIndex) {
                    pagerState.animateScrollToPage(selectedTabIndex)
                }
                LaunchedEffect(pagerState.currentPage) {
                    selectedTabIndex = pagerState.currentPage
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        edgePadding = 16.dp
                    ) {
                        tabNames.forEachIndexed { index, tabName ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(tabName) }
                            )
                        }
                    }

                    val songsForSelectedTab =
                        groupedSongs[tabNames[selectedTabIndex]] ?: emptyList()

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.semantics { traversalIndex = 1f },
                                content = {
                                    items(songsForSelectedTab) { song ->
                                        SongCard(
                                            song = song,
                                            isPlaying = song.id == uiState.currentSong?.id
                                                    && song.artist == uiState.currentSong?.artist
                                                    && song.title == uiState.currentSong?.title
                                                    && uiState.isPlaying,
                                            onPlayClick = { commandHandler.play(song) },
                                            onAddClick = { commandHandler.appendMedia(song) }
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.no_songs_found)
                )
            }
        }
    }
}

fun Float.format(digits: Int): String {
    val factor = 10.0.pow(digits.toDouble())
    return (kotlin.math.round(this * factor) / factor).toString()
}

fun Int.format(digits: Int): String {
    return this.toString().padStart(digits, '0')
}