package com.jetbrains.kmpapp.ui.screens.main.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.feature.commands.MainCommandHandler
import com.jetbrains.kmpapp.ui.components.content.BottomSheetContent
import com.jetbrains.kmpapp.ui.components.content.MainTopAppBar
import com.jetbrains.kmpapp.ui.components.content.SongListHomeView
import com.jetbrains.kmpapp.ui.components.content.TabRowComponent
import com.jetbrains.kmpapp.ui.components.views.ProgressView
import com.jetbrains.kmpapp.ui.screens.main.MainViewModel
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.utils.MainUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.moveBottomSheet
import martyboxapp.composeapp.generated.resources.swipe_down
import martyboxapp.composeapp.generated.resources.swipe_up
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.pow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTypeView(
    mainCommandHandler: MainCommandHandler,
    uiState: MainUiState,
    paddingValues: PaddingValues,
    navigationToQueue: () -> Unit,
    mainViewModel: MainViewModel
) {
    var searchQuery by remember  { mutableStateOf(uiState.searchQuery) }
    val scaffoldState = rememberBottomSheetScaffoldState()

    val animatedSheetContainerColor by animateColorAsState(
        targetValue = when (scaffoldState.bottomSheetState.targetValue) {
            SheetValue.Expanded -> LocalCustomColorsPalette.current.containerSecondary
            SheetValue.PartiallyExpanded -> Color.Transparent
            else -> LocalCustomColorsPalette.current.containerSecondary
        },
        animationSpec = tween(durationMillis = 500)
    )

    val elementsAlpha = remember { Animatable(0f) }
    var currentSheetTarget by remember {
        mutableStateOf(scaffoldState.bottomSheetState.currentValue)
    }

    val groupedSongs by remember(uiState.songs) {
        derivedStateOf { uiState.songs.groupBy { it.tab } }
    }

    var debouncedSearchQuery by remember { mutableStateOf(searchQuery) }
    val isFiltering = remember { mutableStateOf(false) }

    val filteredSongs by produceState(initialValue = emptyList(), debouncedSearchQuery, uiState.songs) {
        isFiltering.value = true
        value = withContext(Dispatchers.Default) {
            if (debouncedSearchQuery.isBlank()) uiState.songs
            else uiState.songs.filter { song ->
                val titleWords = song.title.split(" ").map { it.lowercase() }
                val artistWords = song.artist.split(" ").map { it.lowercase() }
                val queryWords = debouncedSearchQuery.split(" ").map { it.lowercase() }
                queryWords.all { queryWord ->
                    titleWords.any { it.contains(queryWord) } || artistWords.any { it.contains(queryWord) }
                }
            }
        }
        isFiltering.value = false
    }

    var expandedMenu by remember { mutableStateOf(false) }
    var expandedTabIndex by remember { mutableStateOf(-1) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabNames by remember { mutableStateOf( groupedSongs.keys.toList()) }
    val pagerState = rememberPagerState { tabNames.size }

    LaunchedEffect(Unit) {
        elementsAlpha.animateTo(
            targetValue = 0.5f,
            animationSpec = tween(durationMillis = 0)
        )
        elementsAlpha.animateTo(
            targetValue = when (scaffoldState.bottomSheetState.targetValue) {
                SheetValue.Expanded -> 1f
                SheetValue.PartiallyExpanded -> 0f
                else -> 0.5f
            },
            animationSpec = tween(durationMillis = 500)
        )
        currentSheetTarget = scaffoldState.bottomSheetState.targetValue
    }

    LaunchedEffect(scaffoldState.bottomSheetState.targetValue) {
        elementsAlpha.animateTo(
            targetValue = when (scaffoldState.bottomSheetState.targetValue) {
                SheetValue.Expanded -> 1f
                SheetValue.PartiallyExpanded -> 0f
                else -> 0.5f
            },
            animationSpec = tween(durationMillis = 500)
        )

        currentSheetTarget = scaffoldState.bottomSheetState.targetValue
    }

    LaunchedEffect(scaffoldState.bottomSheetState.targetValue) {
        currentSheetTarget = scaffoldState.bottomSheetState.targetValue
    }

    LaunchedEffect(searchQuery) {
        delay(300)
        debouncedSearchQuery = searchQuery
    }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
        delay(1000)
        mainViewModel.updateSongsForTab(tabNames[selectedTabIndex])
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MainTopAppBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onClearSearchQuery = { searchQuery = "" },
                onNavigateToQueue =  { navigationToQueue() }
            )
        },
        containerColor = LocalCustomColorsPalette.current.primaryBackground,
        sheetContainerColor = animatedSheetContainerColor,
        sheetPeekHeight = 150.dp,
        sheetShadowElevation = 0.dp,
        sheetDragHandle = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top
            ) {
                if (currentSheetTarget == SheetValue.Expanded) {
                    Icon(
                        painter = painterResource(Res.drawable.swipe_down),
                        contentDescription = stringResource(Res.string.moveBottomSheet),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.swipe_up),
                        contentDescription = stringResource(Res.string.moveBottomSheet),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        sheetContent = {
            BottomSheetContent(
                uiState = uiState,
                mainCommandHandler = mainCommandHandler,
                elementsAlpha = elementsAlpha.value
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (searchQuery.isNotBlank()) {
                if (isFiltering.value) {
                    ProgressView(contentPadding)
                } else {
                    if (filteredSongs.isEmpty()) {
                        EmptyListView(contentPadding)
                    } else {
                        SongListHomeView(
                            songs = filteredSongs,
                            uiState = uiState,
                            mainCommandHandler = mainCommandHandler
                        )
                    }
                }
            } else {
                if (uiState.songs.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        TabRowComponent(
                            tabNames = tabNames,
                            selectedTabIndex = selectedTabIndex,
                            onTabSelected = { index ->
                                selectedTabIndex = index
                            },
                            onTabLongClick = { index ->
                                expandedTabIndex = index
                                expandedMenu = true
                            }
                        ) {
                            DropdownMenu(
                                expanded = expandedMenu,
                                onDismissRequest = { expandedMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Проиграть случайно папку") },
                                    onClick = {
                                        if (expandedTabIndex in tabNames.indices) {
                                            selectedTabIndex = expandedTabIndex
                                            mainCommandHandler.playTab(tab = tabNames[expandedTabIndex])
                                        }
                                        expandedMenu = false
                                    }
                                )
                            }
                        }

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        ) { page ->
                            AnimatedVisibility(
                                visible = page == pagerState.currentPage,
                                enter = fadeIn(tween(500)),
                                exit = fadeOut(tween(500))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    if (page != pagerState.currentPage) {
                                        // Ничего не отображать, если это не текущая папка
                                    } else if (uiState.isTabLoading) {
                                        ProgressView(paddingValues)
                                    } else {
                                        SongListHomeView(
                                            songs = uiState.currentSongs,
                                            uiState = uiState,
                                            mainCommandHandler = mainCommandHandler
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    EmptyListView(contentPadding)
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