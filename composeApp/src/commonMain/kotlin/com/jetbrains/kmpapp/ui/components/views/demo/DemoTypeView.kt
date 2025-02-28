package com.jetbrains.kmpapp.ui.components.views.demo

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.ui.components.content.MainTopAppBar
import com.jetbrains.kmpapp.ui.components.content.QueueTopAppBar
import com.jetbrains.kmpapp.ui.screens.demo.DemoViewModel
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.utils.DemoUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.moveBottomSheet
import martyboxapp.composeapp.generated.resources.queue
import martyboxapp.composeapp.generated.resources.swipe_down
import martyboxapp.composeapp.generated.resources.swipe_up
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoTypeView(
    uiState: DemoUiState,
    paddingValues: PaddingValues,
    viewModel: DemoViewModel,
    onExitDemoMode: () -> Unit
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

    var isQueueOpen by remember { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()

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

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (isQueueOpen) {
                QueueTopAppBar(
                    type = uiState.serverData.type,
                    title = stringResource(Res.string.queue),
                    onSheetQueueClose = {
                        isQueueOpen = false
                        viewModel.clearQueue()
                    },
                    onClearQueue = { viewModel.clearQueue() }
                )
            } else {
                MainTopAppBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onClearSearchQuery = { searchQuery = "" },
                    onNavigateToQueue = {
                        viewModel.updateQueue()
                        isQueueOpen = true
                    },
                    onNavigateToHome = {

                    }
                )
            }

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
            DemoBottomSheetContentView(
                uiState = uiState,
                viewModel = viewModel,
                elementsAlpha = elementsAlpha.value,
                onExitDemoMode = {
                    onExitDemoMode()
                }
            )
        }
    ) { contentPadding ->
        if (isQueueOpen) {
            QueueDemoContentView(
                uiState = uiState,
                viewModel = viewModel,
                lazyListState = lazyListState,
                contentPadding = contentPadding,
                paddingValues = paddingValues
            )
        } else {
            DemoContentView(
                uiState = uiState,
                viewModel = viewModel,
                isFiltering = isFiltering,
                filteredSongs = filteredSongs,
                searchQuery = searchQuery,
                contentPadding = contentPadding,
                paddingValues = paddingValues,
            )
        }
    }
}