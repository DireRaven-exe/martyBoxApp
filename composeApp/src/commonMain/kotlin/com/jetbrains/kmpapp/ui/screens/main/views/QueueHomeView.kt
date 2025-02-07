package com.jetbrains.kmpapp.ui.screens.main.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jetbrains.kmpapp.feature.commands.CommandHandler
import com.jetbrains.kmpapp.ui.components.views.QueueHomeContentView
import com.jetbrains.kmpapp.ui.components.content.QueueTopAppBar
import com.jetbrains.kmpapp.utils.MainUiState
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.queue
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueHomeView(
    uiState: MainUiState,
    commandHandler: CommandHandler,
    paddingValues: PaddingValues,
    onSheetQueueClose: () -> Unit
) {
    val lazyListState = rememberLazyListState()

    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
        ),
        onDismissRequest = { onSheetQueueClose() }
    ) {
        Scaffold(
            topBar = {
                QueueTopAppBar(
                    title = stringResource(Res.string.queue),
                    onSheetQueueClose = onSheetQueueClose
                )
            },
            modifier = Modifier
                .fillMaxSize()
        ) { contentPadding ->
            QueueHomeContentView(
                uiState = uiState,
                commandHandler = commandHandler,
                paddingValues = contentPadding,
                lazyListState = lazyListState,
                contentPadding = contentPadding
            )

        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun QueueHomeView(
//    uiState: MainUiState,
//    mainCommandHandler: MainCommandHandler,
//    paddingValues: PaddingValues,
//    onSheetQueueClose: () -> Unit
//) {
//    val sheetState = rememberModalBottomSheetState(
//        skipPartiallyExpanded = true,
//        confirmValueChange = { it != SheetValue.Hidden && it != SheetValue.PartiallyExpanded }
//    )
//    val lazyListState = rememberLazyListState()
//
//    LaunchedEffect(sheetState.isVisible) {
//        if (!sheetState.isVisible) {
//            sheetState.show()
//        }
//    }
//
//    ModalBottomSheet(
//        onDismissRequest = { onSheetQueueClose() },
//        sheetState = sheetState,
//        containerColor = LocalCustomColorsPalette.current.primaryBackground,
//        dragHandle = {
//            QueueTopAppBar(
//                title = stringResource(Res.string.queue),
//                onSheetQueueClose = onSheetQueueClose
//            )
//        },
//    ) {
//        QueueHomeContentView(
//            uiState = uiState,
//            lazyListState = lazyListState,
//            mainCommandHandler = mainCommandHandler,
//            paddingValues = paddingValues
//        )
//    }
//}

