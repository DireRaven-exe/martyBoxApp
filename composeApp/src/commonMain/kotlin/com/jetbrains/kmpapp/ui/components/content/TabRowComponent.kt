package com.jetbrains.kmpapp.ui.components.content

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabRowComponent(
    tabNames: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onTabLongClick: (Int) -> Unit,
    content: @Composable () -> Unit = {}
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 16.dp,
        containerColor = LocalCustomColorsPalette.current.primaryBackground
    ) {
        tabNames.forEachIndexed { index, tabName ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { onTabSelected(index) },
                        onLongClick = { onTabLongClick(index) }
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = tabName,
                        color = animateColorAsState(
                            targetValue = if (selectedTabIndex == index) LocalCustomColorsPalette.current.primaryText else LocalCustomColorsPalette.current.secondaryText,
                            animationSpec = tween(durationMillis = 500)
                        ).value,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
        content()
    }
}