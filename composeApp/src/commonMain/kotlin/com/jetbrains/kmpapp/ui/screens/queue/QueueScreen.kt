@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.jetbrains.kmpapp.ui.screens.queue

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.jetbrains.kmpapp.domain.models.SongInQueue
import com.jetbrains.kmpapp.feature.commands.QueueCommandHandler
import com.jetbrains.kmpapp.feature.vibration.HapticFeedback
import com.jetbrains.kmpapp.feature.vibration.HapticFeedbackType
import com.jetbrains.kmpapp.feature.vibration.provideHapticFeedback
import com.jetbrains.kmpapp.ui.components.dialogs.ConfirmDisconnectionDialog
import com.jetbrains.kmpapp.ui.components.dialogs.ServerDisconnectedDialog
import com.jetbrains.kmpapp.ui.components.content.SongCard
import com.jetbrains.kmpapp.ui.navigation.NavigationItem
import com.jetbrains.kmpapp.ui.screens.loading.LoadingScreen
import com.jetbrains.kmpapp.ui.screens.main.views.EmptyListView
import com.jetbrains.kmpapp.ui.screens.main.views.format
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.utils.Constants
import com.jetbrains.kmpapp.utils.QueueUiState
import io.github.aakira.napier.Napier
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.artist
import martyboxapp.composeapp.generated.resources.autoFullScreen
import martyboxapp.composeapp.generated.resources.moveBottomSheet
import martyboxapp.composeapp.generated.resources.music_plus
import martyboxapp.composeapp.generated.resources.next
import martyboxapp.composeapp.generated.resources.orders
import martyboxapp.composeapp.generated.resources.pause
import martyboxapp.composeapp.generated.resources.pitch
import martyboxapp.composeapp.generated.resources.queue
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
import org.koin.compose.koinInject
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun QueueScreen(
    navController: NavHostController,
    queueViewModel: QueueViewModel = koinInject(),
    paddingValues: PaddingValues
) {
    val uiState by queueViewModel.uiState.collectAsStateWithLifecycle()
    val savedQrCode = uiState.savedQrCode
    val currentSong = uiState.currentSong
    val currentPlaylist = remember(uiState.currentPlaylist) { uiState.currentPlaylist }

    Napier.e(tag = "SAVEDTABLE", message = "QUEUE TABLE: ${uiState.currentTable.toString()}")
    val commandHandler = QueueCommandHandler(queueViewModel)
    val hapticFeedback = provideHapticFeedback()

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        commandHandler.moveOn(from.index, to.index)
        queueViewModel.moveSongInQueue(from.index, to.index)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.Heavy)
    }

    var isReconnectAllowed  by rememberSaveable { mutableStateOf(true) }
    var showDisconnectedDialog by rememberSaveable { mutableStateOf(false) }
    var showConfirmDisconnectionDialog = false
    var isManuallyDisconnected by rememberSaveable { mutableStateOf(false) }

    var selectedSong by remember { mutableStateOf<SongInQueue?>(null) }
    var selectedIndex by remember { mutableStateOf<Int>(-1) }
    var showContextMenu by remember { mutableStateOf(false) }


    LaunchedEffect(savedQrCode) {
        savedQrCode.let {
            if (isReconnectAllowed) {
                queueViewModel.connectToWebSocket(it)
            }
        }
    }

    LaunchedEffect(uiState.isServerConnected) {
//        if (!uiState.isServerConnected && navController.currentBackStackEntry?.destination?.route != NavigationItem.Home.route) {
//            showDisconnectedDialog = true
//        }
        if (!uiState.isServerConnected) {
            showDisconnectedDialog = true // Показываем диалог при отключении
        }
    }

    if (showDisconnectedDialog && !isManuallyDisconnected) {
        ServerDisconnectedDialog(
            onDisconnect = {
                showDisconnectedDialog = false
                isReconnectAllowed = false
                queueViewModel.clearSavedQrCode()
                queueViewModel.clearSavedTableNumber()
                navController.navigate(NavigationItem.Home.route) {
                    popUpTo(NavigationItem.Home.route) { inclusive = true }
                }
                queueViewModel.onDisconnected("Connection lost")
            },
            onReconnect = {
                showDisconnectedDialog = false
                isReconnectAllowed = true
//                queueViewModel.webSocketClient.reset()
//                queueViewModel.updateIsLoading(true)
                queueViewModel.connectToWebSocket(savedQrCode)
            }
        )
    }

    if (showConfirmDisconnectionDialog) {
        ConfirmDisconnectionDialog(
            onConfirm = {
                showConfirmDisconnectionDialog = false
                isManuallyDisconnected = true
                isReconnectAllowed = false
                queueViewModel.clearSavedQrCode()
                queueViewModel.clearSavedTableNumber()
                navController.navigate(NavigationItem.Home.route) {
                    popUpTo(NavigationItem.Home.route) { inclusive = true }
                }
                queueViewModel.onDisconnected("User manually disconnected")
            },
            onDismiss = {
                showConfirmDisconnectionDialog = false
            }
        )
    }
    if (uiState.isLoading) {
        LoadingScreen(
            onCancelClick = {
                isManuallyDisconnected = true
                isReconnectAllowed = false
                queueViewModel.clearSavedQrCode()
                queueViewModel.clearSavedTableNumber()
                navController.navigate(NavigationItem.Home.route) {
                    popUpTo(NavigationItem.Home.route) { inclusive = true }
                }
                queueViewModel.onDisconnected("")
            }
        )
    } else {
        when (uiState.serverData.type) {
            Constants.TYPE_HOME -> {
                PlaylistView(
                    navController,
                    currentPlaylist,
                    lazyListState,
                    selectedSong,
                    selectedIndex,
                    showContextMenu,
                    currentSong,
                    uiState,
                    commandHandler,
                    hapticFeedback,
                    queueViewModel,
                    reorderableLazyListState
                )
            }
            Constants.TYPE_CLUB -> {
                OrdersView(
                    navController,
                    currentPlaylist,
                    currentSong,
                    uiState,
                    lazyListState,
                    selectedSong,
                    selectedIndex,
                    commandHandler,
                    queueViewModel
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PlaylistView(
    navController: NavHostController,
    currentPlaylist: MutableList<SongInQueue>,
    lazyListState: LazyListState,
    selectedSong: SongInQueue?,
    selectedIndex: Int,
    showContextMenu: Boolean,
    currentSong: SongInQueue?,
    uiState: QueueUiState,
    commandHandler: QueueCommandHandler,
    hapticFeedback: HapticFeedback,
    queueViewModel: QueueViewModel,
    reorderableLazyListState: ReorderableLazyListState

) {
    var selectedSong1 = selectedSong
    var selectedIndex1 = selectedIndex
    var showContextMenu1 = showContextMenu

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

    LaunchedEffect(Unit) {
        // Pre-initialize animations and states
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


    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.queue)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(NavigationItem.Main.route) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { commandHandler.clearPlaylist() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Очистить очередь",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
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
                            text = uiState.currentSong?.title ?: stringResource(Res.string.title),
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            fontWeight = FontWeight.Bold,
                            color = LocalCustomColorsPalette.current.primaryText
                        )

                        Text(
                            text = uiState.currentSong?.artist ?: stringResource(Res.string.artist),
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
                        onClick = {
                            commandHandler.next()
                            queueViewModel.updateSongs()
                                  },
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
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (currentPlaylist.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    itemsIndexed(
                        currentPlaylist,
                        key = { _, item -> item.key }) { index, item ->
                        ReorderableItem(
                            reorderableLazyListState,
                            key = item.key
                        ) { isDragging ->
                            val interactionSource = remember { MutableInteractionSource() }

                            Card(
                                onClick = {
                                    selectedSong1 = item
                                    selectedIndex1 = index
                                    showContextMenu1 = true
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDragging) {
                                        LocalCustomColorsPalette.current.cardCurrentSongBackground
                                    } else {
                                        LocalCustomColorsPalette.current.primaryBackground
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(0.dp),
                                modifier = Modifier
                                    .semantics {
                                        // Ваши CustomAccessibilityActions
                                    },
                                interactionSource = interactionSource,
                            ) {
                                SongCard(
                                    song = item,
                                    isPlaying = item == currentSong && uiState.isPlaying,
                                    isCurrentSong = item == currentSong,
                                    onPlayClick = {
                                        commandHandler.playSoundInPlaylist(index, song = item)
                                    },
                                    modifier = Modifier.draggableHandle(
                                        onDragStarted = {
                                            hapticFeedback.performHapticFeedback(
                                                HapticFeedbackType.Heavy
                                            )
                                        },
                                        onDragStopped = {
                                            hapticFeedback.performHapticFeedback(
                                                HapticFeedbackType.Heavy
                                            )
                                        },
                                        interactionSource = interactionSource
                                    ),
                                )
                            }
                        }
                    }
                }
            } else {
                EmptyListView(contentPadding)
            }
        }
    }

    if (showContextMenu1 && selectedSong1 != null) {
        ContextMenu(
            song = selectedSong1!!,
            onDismiss = { showContextMenu1 = false },
            onDelete = {
                queueViewModel.removeSong(selectedSong1!!)
                commandHandler.removeSoundFromPlaylist(currentPlaylist.indexOf(selectedSong1))
                showContextMenu1 = false
            },
            onSetPitch = { newPitch ->
                commandHandler.setPitchToFile(selectedIndex1, newPitch)
                showContextMenu1 = false
            }
        )
    }
}

@Composable
fun OrdersView(
    navController: NavHostController,
    currentPlaylist: MutableList<SongInQueue>,
    currentSong: SongInQueue?,
    uiState: QueueUiState,
    lazyListState: LazyListState,
    selectedSong: SongInQueue?,
    selectedIndex: Int,
    commandHandler: QueueCommandHandler,
    queueViewModel: QueueViewModel,
) {
    var selectedSong1 = selectedSong
    var selectedIndex1 = selectedIndex

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.orders)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(NavigationItem.Main.route) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        containerColor = LocalCustomColorsPalette.current.primaryBackground
    )
    { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (currentPlaylist.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    itemsIndexed(
                        currentPlaylist,
                        key = { _, item -> item.key }) { index, item ->
                            Card(
                                onClick = {
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor =
                                        LocalCustomColorsPalette.current.primaryBackground
                                ),
                                elevation = CardDefaults.cardElevation(0.dp),
                                modifier = Modifier
                            ) {
                                SongCard(
                                    song = item,
                                    onDeleteSong = {
                                        commandHandler.removeSoundFromPlaylist(index)
                                    },
                                )
                            }
                        }
                    }
                } else {
                EmptyListView(contentPadding)
            }
        }
    }
}


@Composable
fun ContextMenu(
    song: SongInQueue,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onSetPitch: (Int) -> Unit
) {
    var showPitchDialog by remember { mutableStateOf(false) } // Состояние для отображения диалога с тональностью
    var pitch by remember { mutableStateOf(0) } // Состояние для хранения выбранной тональности

    // Основной диалог с действиями
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Выберите действие", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                // Кнопка "Указать тональность"
                Button(
                    onClick = { showPitchDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Указать тональность",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Указать тональность",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Кнопка "Удалить"
                Button(
                    onClick = {
                        onDelete()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Удалить",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )

    // Диалог для выбора тональности
    if (showPitchDialog) {
        AlertDialog(
            onDismissRequest = { showPitchDialog = false },
            title = { Text("Выберите тональность", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column {
                    // Слайдер для выбора тональности
                    Slider(
                        value = pitch.toFloat(),
                        onValueChange = { pitch = it.roundToInt() },
                        valueRange = -7f..7f,
                        steps = 13, // От -7 до +7 с шагом 1
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    // Отображение текущей тональности
                    Text(
                        text = "Текущая тональность: $pitch",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetPitch(pitch) // Применяем выбранную тональность
                        showPitchDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Применить", style = MaterialTheme.typography.bodyLarge)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showPitchDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text("Отмена", style = MaterialTheme.typography.bodyLarge)
                }
            }
        )
    }
}


enum class QueueMode { HOME, CLUB }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueTopBar(
    navController: NavHostController,
    mode: String,
    onClearQueue: () -> Unit // Функция для очистки очереди
) {
    TopAppBar(
        title = { Text(text = if (mode == Constants.TYPE_HOME) "Очередь" else "Мои заказы") },
        navigationIcon = {
            IconButton(onClick = { navController.navigate(NavigationItem.Main.route) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            if (mode == Constants.TYPE_HOME) {
                IconButton(
                    onClick = onClearQueue, // Обработчик очистки очереди
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear, // Иконка для очистки
                        contentDescription = "Очистить очередь",
                        tint = MaterialTheme.colorScheme.onSurface // Цвет иконки
                    )
                }
            }
        }
    )
}


