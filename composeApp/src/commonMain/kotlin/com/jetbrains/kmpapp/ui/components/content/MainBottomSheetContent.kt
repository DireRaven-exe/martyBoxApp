package com.jetbrains.kmpapp.ui.components.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.feature.commands.MainCommandHandler
import com.jetbrains.kmpapp.ui.screens.main.views.format
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.utils.MainUiState
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.artist
import martyboxapp.composeapp.generated.resources.autoFullScreen
import martyboxapp.composeapp.generated.resources.music_plus
import martyboxapp.composeapp.generated.resources.next
import martyboxapp.composeapp.generated.resources.pause
import martyboxapp.composeapp.generated.resources.pitch
import martyboxapp.composeapp.generated.resources.singingAssessment
import martyboxapp.composeapp.generated.resources.soundInPause
import martyboxapp.composeapp.generated.resources.stop
import martyboxapp.composeapp.generated.resources.tempo
import martyboxapp.composeapp.generated.resources.title
import martyboxapp.composeapp.generated.resources.volume
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BottomSheetContent(
    uiState: MainUiState,
    mainCommandHandler: MainCommandHandler,
    elementsAlpha: Float
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
                if (!uiState.isPlaying) mainCommandHandler.playAfterPause()
                else mainCommandHandler.pause()
            }) {
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
            IconButton(onClick = { mainCommandHandler.next() }) {
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
        modifier = Modifier.alpha(elementsAlpha)
            .padding(start = 24.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                enabled = uiState.hasPlus,
                onClick = { mainCommandHandler.switchPlusMinus() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.music_plus),
                    contentDescription = "Has plus",
                    modifier = Modifier.size(28.dp),
                )
            }
            IconButton(onClick = { mainCommandHandler.stop() }) {
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
                mainCommandHandler.volume(it)
                mainCommandHandler.updateVolume(it)
            },
            valueRange = 0f..1f,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
        )

        Text(text = stringResource(Res.string.tempo) + ": ${uiState.tempo.format(2)}x")
        Slider(
            value = uiState.tempo,
            onValueChange = {
                mainCommandHandler.changeTempo(it)
                mainCommandHandler.updateTempo(it)
            },
            valueRange = 0.5f..1.5f,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
        )

        Text(stringResource(Res.string.pitch) + ": ${uiState.pitch.format(1)}")
        Slider(
            value = uiState.pitch.toFloat(),
            onValueChange = {
                mainCommandHandler.changePitch(it.roundToInt())
                mainCommandHandler.updatePitch(it.roundToInt())
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
                        mainCommandHandler.changeAutoFullScreen(it)
                        mainCommandHandler.updateAutoFullScreen(it)
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
                        mainCommandHandler.changeSingingAssessment(it)
                        mainCommandHandler.updateSingingAssessment(it)
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
                        mainCommandHandler.changeSoundInPause(it)
                        mainCommandHandler.updateSoundInPause(it)
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