@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.jetbrains.kmpapp.ui.screens.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.map
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.accept
import martyboxapp.composeapp.generated.resources.cancel
import martyboxapp.composeapp.generated.resources.filters
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    navigator: NavHostController,
    paddingValues: PaddingValues,
    viewModel: FilterViewModel = koinInject()
) {
    val selectedFilters by viewModel.uiState.map { it.selectedFilters }.collectAsState( emptyMap() )

    val uiState by viewModel.uiState.collectAsState()
    val optionsState by viewModel.optionsState.collectAsState()
    val availableKeys by viewModel.availableKeys.collectAsState()

    BottomSheetScaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarColors(
                    containerColor = LocalCustomColorsPalette.current.primaryBackground,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = LocalCustomColorsPalette.current.selectedIcon,
                    scrolledContainerColor = LocalCustomColorsPalette.current.primaryBackground,
                    actionIconContentColor = LocalCustomColorsPalette.current.unselectedIcon
                ),
                title = {
                    Text(
                        text = stringResource(Res.string.filters),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedFilters.values.all { it.isEmpty() }) {
                                viewModel.clearFilters()
                            } else {
                                viewModel.updateFilters(selectedFilters.toMap())
                            }
                            navigator.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        sheetContent = {
            Box(
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(16.dp)
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(64.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            viewModel.clearFilters()
                            navigator.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LocalCustomColorsPalette.current.defaultButtonContainer,
                            contentColor = LocalCustomColorsPalette.current.defaultButtonContent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .width(140.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.cancel),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Button(
                        onClick = {
                            if (selectedFilters.values.all { it.isEmpty() }) {
                                viewModel.clearFilters()
                            } else {
                                viewModel.updateFilters(selectedFilters.toMap())
                            }
                            Napier.e(tag = "ABOBA", message = selectedFilters.toString())
                            viewModel.updateSearch()
                            navigator.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LocalCustomColorsPalette.current.buttonContainer,
                            contentColor = LocalCustomColorsPalette.current.buttonContent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .width(140.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.accept),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        sheetDragHandle = {  },
        sheetSwipeEnabled = false,
        sheetPeekHeight = 140.dp,
        containerColor = LocalCustomColorsPalette.current.primaryBackground,
        sheetContainerColor = LocalCustomColorsPalette.current.containerSecondary,
        sheetShadowElevation = 8.dp,

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Switch(
                        checked = uiState.artistSearchActive,
                        onCheckedChange = { isChecked ->
                            if (isChecked || uiState.titleSearchActive) {
                                viewModel.updateArtistSearchActive(isChecked)
                            }
                        }
                    )
                    Text(
                        text = "Поиск по исполнителю", // stringResource(Res.string.artist),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                }

                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Switch(
                        checked = uiState.titleSearchActive,
                        onCheckedChange = { isChecked ->
                            if (isChecked || uiState.artistSearchActive) {
                                viewModel.updateTitleSearchActive(isChecked)
                            }
                        }
                    )
                    Text(
                        text = "Поиск по названию", // stringResource(Res.string.title),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                }

                availableKeys.forEach { key ->
                    LaunchedEffect(key) {
                        viewModel.fetchOptions(key)
                    }

                    val optionsForKey = optionsState[key] ?: emptyList()

                    if (optionsForKey.isEmpty()) {
                        Text(
                            text = "Загрузка опций для $key...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        val selectedOptions = uiState.selectedFilters[key] ?: listOf()

                        FilterSection(
                            title = viewModel.getSectionTitle(key),
                            options = optionsForKey,
                            selectedOptions = selectedOptions,
                            onOptionToggle = { option ->
                                viewModel.updateFilter(key, option)
                            },
                            onSelectAll = {
                                viewModel.addAllOptionsToFilter(key, optionsForKey)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSection(
    title: String,
    options: List<String>,
    selectedOptions: List<String>,
    onOptionToggle: (String) -> Unit,
    onSelectAll: (List<String>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
            TextButton(onClick = { onSelectAll(options) }) {
                Text(
                    text = "Все",
                    color = LocalCustomColorsPalette.current.selectedIcon,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp
                )
            }
        }

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
        ) {
            options.forEach { option ->
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onOptionToggle(option)
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedOptions.contains(option)) LocalCustomColorsPalette.current.selectedIcon else LocalCustomColorsPalette.current.containerSecondary
                    )
                ) {
                    Text(
                        text = option,
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 14.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = if (selectedOptions.contains(option)) LocalCustomColorsPalette.current.selectedText else LocalCustomColorsPalette.current.primaryText
                    )
                }
            }
        }
    }
}