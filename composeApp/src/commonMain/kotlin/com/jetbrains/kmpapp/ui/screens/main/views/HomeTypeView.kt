@file:OptIn(ExperimentalMaterial3Api::class)

package com.jetbrains.kmpapp.ui.screens.main.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.feature.commands.CommandHandler
import com.jetbrains.kmpapp.ui.components.CustomSearchView
import com.jetbrains.kmpapp.ui.components.SongCard
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.utils.MainUiState
import com.skydoves.flexible.bottomsheet.material3.FlexibleBottomSheet
import com.skydoves.flexible.core.FlexibleSheetSize
import com.skydoves.flexible.core.FlexibleSheetValue
import com.skydoves.flexible.core.rememberFlexibleBottomSheetState
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.artist
import martyboxapp.composeapp.generated.resources.autoFullScreen
import martyboxapp.composeapp.generated.resources.filter_icon
import martyboxapp.composeapp.generated.resources.moveBottomSheet
import martyboxapp.composeapp.generated.resources.music_plus
import martyboxapp.composeapp.generated.resources.next
import martyboxapp.composeapp.generated.resources.pause
import martyboxapp.composeapp.generated.resources.pitch
import martyboxapp.composeapp.generated.resources.singingAssessment
import martyboxapp.composeapp.generated.resources.soundInPause
import martyboxapp.composeapp.generated.resources.stop
import martyboxapp.composeapp.generated.resources.swipe_down
import martyboxapp.composeapp.generated.resources.swipe_up
import martyboxapp.composeapp.generated.resources.tempo
import martyboxapp.composeapp.generated.resources.title
import martyboxapp.composeapp.generated.resources.volume
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.pow
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeTypeView(
    commandHandler: CommandHandler,
    uiState: MainUiState,
    navigateToFilter: () -> Unit,
    filtersState: Map<String, List<String>>,
    paddingValues: PaddingValues
) {
    var searchQuery by rememberSaveable { mutableStateOf(uiState.searchQuery) }
    var expanded by rememberSaveable { mutableStateOf(uiState.searchBarActive) }


    val currentSong = uiState.currentSong

    val sheetState = rememberFlexibleBottomSheetState(
        flexibleSheetSize = FlexibleSheetSize(fullyExpanded = 0.5f, intermediatelyExpanded = 0.25f , slightlyExpanded = 0.12f),
        isModal = false,
        skipSlightlyExpanded = false,
        skipHiddenState = true,
        skipIntermediatelyExpanded = true
    )


    val animatedSheetContainerColor by animateColorAsState(
        targetValue = when (sheetState.targetValue) {
            FlexibleSheetValue.FullyExpanded -> LocalCustomColorsPalette.current.containerSecondary
            FlexibleSheetValue.SlightlyExpanded -> Color.Transparent
            else -> LocalCustomColorsPalette.current.containerSecondary
        },
        animationSpec = tween(durationMillis = 300)
    )

    val elementsAlpha = remember { Animatable(0f) }
    val offset = remember { Animatable(30f) }
    var currentSheetTarget by remember {
        mutableStateOf(sheetState.targetValue)
    }

    LaunchedEffect(sheetState.targetValue) {
        elementsAlpha.animateTo(
            targetValue = when (sheetState.targetValue) {
                FlexibleSheetValue.FullyExpanded -> 1f
                FlexibleSheetValue.SlightlyExpanded -> 0f
                else -> 0.5f
            },
            animationSpec = tween(durationMillis = 300)
        )

        offset.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 300)
        )
        currentSheetTarget = sheetState.targetValue
    }

    LaunchedEffect(sheetState.targetValue) {
        currentSheetTarget = sheetState.targetValue
    }

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

    val filteredSongs = remember(searchQuery, uiState.songs, uiState.artistSearchActive, uiState.titleSearchActive) {
        if (searchQuery.isBlank()) {
            uiState.songs
        } else {
            uiState.songs.filter { song ->
                val titleWords = song.title.split(" ").map { it.lowercase() }
                val artistWords = song.artist.split(" ").map { it.lowercase() }
                val queryWords = searchQuery.split(" ").map { it.lowercase() }

                queryWords.all { queryWord ->
                    when {
                        uiState.artistSearchActive && uiState.titleSearchActive -> {
                            titleWords.any { it.contains(queryWord) } || artistWords.any { it.contains(queryWord) }
                        }
                        uiState.artistSearchActive -> {
                            artistWords.any { it.contains(queryWord) }
                        }
                        uiState.titleSearchActive -> {
                            titleWords.any { it.contains(queryWord) }
                        }
                        else -> false // Если оба переключателя выключены (не должно быть такого, но на всякий случай).
                    }
                }
            }
        }
    }

    val finalFilteredSongs = remember(filteredSongs, filtersState) {
        filteredSongs?.filter { song ->
            filtersState.all { (key, values) ->
                when (key) {
                    "artist" -> values.any { song.artist.contains(it, ignoreCase = true) }
                    else -> true
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomSearchView(
                    search = searchQuery,
                    onValueChange = { searchQuery = it },
                    onClearSearchQuery = { searchQuery = "" },
                    modifier = Modifier
                        .height(50.dp)
                        .weight(0.9f)
                        .padding(end = 16.dp)
                )
                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = {
                        commandHandler.setTitleSearchActive(uiState.titleSearchActive)
                        commandHandler.setArtistSearchActive(uiState.artistSearchActive)
                        navigateToFilter()
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.filter_icon),
                        modifier = Modifier.size(24.dp),
                        tint = LocalCustomColorsPalette.current.primaryIcon,
                        contentDescription = "Фильтры"
                    )
                }
            }
        },
        containerColor = LocalCustomColorsPalette.current.primaryBackground,
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.TopCenter
        ) {

            if (searchQuery.isNotBlank() || filtersState.isNotEmpty()) {
                if (finalFilteredSongs!!.isEmpty()) {
                    EmptyListView(contentPadding)
                } else
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    content = {
                        items(finalFilteredSongs!!) { song ->
                            SongCard(
                                song = song,
                                isPlaying = song.id == uiState.currentSong?.id
                                        && song.artist == uiState.currentSong?.artist
                                        && song.title == uiState.currentSong?.title
                                        && uiState.isPlaying,
                                isCurrentSong = song == uiState.currentSong,
                                onPlayClick = { commandHandler.play(song) },
                                onAddClick = { commandHandler.appendMedia(song) }
                            )
                        }
                    }
                )
            } else
                if (uiState.songs.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
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
                                .weight(1f),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.TopCenter
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
                                        items(songsForSelectedTab) { song ->
                                            SongCard(
                                                song = song,
                                                isPlaying = song.id == uiState.currentSong?.id
                                                        && song.artist == uiState.currentSong?.artist
                                                        && song.title == uiState.currentSong?.title
                                                        && uiState.isPlaying,
                                                onPlayClick = { commandHandler.play(song) },
                                                onAddClick = { commandHandler.appendMedia(song) },
                                                isCurrentSong = song == uiState.currentSong,
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                } else {
                    EmptyListView(contentPadding)
                }
            FlexibleBottomSheet(
                windowInsets = WindowInsets(0),
                sheetState = sheetState,
                onDismissRequest = { },
                containerColor = animatedSheetContainerColor,
                dragHandle = {
                    Row(
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Top
                    ) {
                        if (currentSheetTarget == FlexibleSheetValue.FullyExpanded) {
                            Icon(
                                painter = painterResource(Res.drawable.swipe_down), // Стрелка вниз
                                contentDescription = stringResource(Res.string.moveBottomSheet),
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(Res.drawable.swipe_up), // Стрелка вверх
                                contentDescription = stringResource(Res.string.moveBottomSheet),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color = LocalCustomColorsPalette.current.cardCurrentSongBackground)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        ) {
                            Text(
                                text = currentSong?.title ?: stringResource(Res.string.title),
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                fontWeight = FontWeight.Bold,
                                color = LocalCustomColorsPalette.current.primaryText
                            )

                            Text(
                                text = currentSong?.artist ?: stringResource(Res.string.artist),
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                color = LocalCustomColorsPalette.current.secondaryText
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
                                    modifier = Modifier.size(32.dp)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(Res.drawable.pause),
                                    contentDescription = "Pause",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = { commandHandler.next() },
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.next),
                                contentDescription = "Next",
                                modifier = Modifier.size(32.dp),
                                tint = LocalCustomColorsPalette.current.secondaryIcon
                            )
                        }

                    }
                }

                if (currentSheetTarget != FlexibleSheetValue.SlightlyExpanded) {
                    Column(
                        modifier = Modifier.alpha(elementsAlpha.value)
                            .padding(start = 24.dp, bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                enabled = uiState.hasPlus,
                                onClick = { commandHandler.switchPlusMinus() }
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.music_plus),
                                    contentDescription = "Has plus",
                                    modifier = Modifier.size(28.dp),
                                )
                            }
                            IconButton(
                                onClick = {
                                    commandHandler.stop()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.stop),
                                    contentDescription = "Stop",
                                    modifier = Modifier.size(36.dp),
                                    tint = LocalCustomColorsPalette.current.primaryIcon
                                )
                            }
                            IconButton(
                                onClick = {

                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Stop",
                                    modifier = Modifier.size(36.dp),
                                    tint = LocalCustomColorsPalette.current.primaryIcon
                                )
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

                        Text(stringResource(Res.string.pitch) + ": ${uiState.pitch.format(1)}")
                        Slider(
                            value = uiState.pitch.toFloat(),
                            onValueChange = {
                                commandHandler.changePitch(it.roundToInt())
                                commandHandler.updatePitch(it.roundToInt())
                            },
                            valueRange = -7f..7f,
                            steps = 13,
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                        )
                        FlowRow(
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
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
                    }
                }
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