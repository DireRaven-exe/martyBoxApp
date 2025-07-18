package com.jetbrains.kmpapp.ui.components.views.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.feature.commands.CommandHandler
import com.jetbrains.kmpapp.ui.components.views.SlidersView
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import com.jetbrains.kmpapp.ui.theme.songCardPrimaryContent
import com.jetbrains.kmpapp.ui.theme.songCardSecondaryContent
import com.jetbrains.kmpapp.utils.Constants
import com.jetbrains.kmpapp.utils.MainUiState
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.artist
import martyboxapp.composeapp.generated.resources.next
import martyboxapp.composeapp.generated.resources.noshuffle
import martyboxapp.composeapp.generated.resources.pause
import martyboxapp.composeapp.generated.resources.play_playlist
import martyboxapp.composeapp.generated.resources.play_tab
import martyboxapp.composeapp.generated.resources.shuffle
import martyboxapp.composeapp.generated.resources.singingAssessment
import martyboxapp.composeapp.generated.resources.soundInPause
import martyboxapp.composeapp.generated.resources.stop
import martyboxapp.composeapp.generated.resources.title
import martyboxapp.composeapp.generated.resources.voice
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainBottomSheetContentView(
    uiState: MainUiState,
    commandHandler: CommandHandler,
    elementsAlpha: Float,
    onPlayingOrderChanged: () -> Unit
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
                    color = songCardPrimaryContent
                )

                Text(
                    text = uiState.currentSong?.artist ?: stringResource(Res.string.artist),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    color = songCardSecondaryContent
                )
            }
            IconButton(onClick = { commandHandler.stop() }) {
                Icon(
                    painter = painterResource(Res.drawable.stop),
                    contentDescription = "Stop",
                    modifier = Modifier.size(36.dp),
                    tint = LocalCustomColorsPalette.current.primaryIcon
                )
            }
            IconButton(onClick = {
                if (!uiState.isPlaying) commandHandler.playAfterPause()
                else commandHandler.pause()
            }) {
                if (!uiState.isPlaying) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(32.dp),
                        tint = songCardPrimaryContent
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.pause),
                        contentDescription = "Pause",
                        modifier = Modifier.size(32.dp),
                        tint = songCardPrimaryContent
                    )
                }
            }

            IconButton(onClick = { commandHandler.next() }) {
                Icon(
                    painter = painterResource(Res.drawable.next),
                    contentDescription = "Next",
                    modifier = Modifier.size(32.dp),
                    tint = songCardSecondaryContent
                )
            }
        }
    }
    Column(
        modifier = Modifier.alpha(elementsAlpha)
            .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LocalCustomColorsPalette.current.cardCurrentSongBackground
                    ),
                    shape = RoundedCornerShape(10.dp),
                    onClick = { commandHandler.playTab(Constants.PLAYLIST_TAB_NAME) }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.play_tab),
                        contentDescription = "playTab",
                        modifier = Modifier.size(32.dp),
                        tint = songCardSecondaryContent
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = stringResource(Res.string.play_playlist),
                        color = LocalCustomColorsPalette.current.secondaryText
                    )
                }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    enabled = uiState.hasPlus,
                    onClick = { commandHandler.switchPlusMinus() }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.voice),
                        contentDescription = "Has plus",
                        modifier = Modifier.size(28.dp),
                    )
                }


                IconButton(onClick = {
                    commandHandler.changePlayingOrder(!uiState.playingOrder)
                    onPlayingOrderChanged()
                }) {
                    if (uiState.playingOrder) {
                        Icon(
                            painter = painterResource(Res.drawable.shuffle),
                            contentDescription = "Play",
                            modifier = Modifier.size(32.dp),
                            tint = songCardPrimaryContent
                        )
                    } else {
                        Icon(
                            painter = painterResource(Res.drawable.noshuffle),
                            contentDescription = "Pause",
                            modifier = Modifier.size(32.dp),
                            tint = songCardPrimaryContent
                        )
                    }
                }
            }
        }

        SlidersView(
            uiState = uiState,
            commandHandler = commandHandler
        )

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Раздвигаем элементы
            ) {
                Row(
                    modifier = Modifier.weight(1f), // Левый переключатель
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
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2
                    )
                }

                Row(
                    modifier = Modifier.weight(1f), // Правый переключатель
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End // Выравниваем вправо
                ) {
                    Switch(
                        checked = uiState.soundInPause,
                        modifier = Modifier.padding(end = 8.dp),
                        onCheckedChange = {
                            commandHandler.changeSoundInPause(it)
                            commandHandler.updateSoundInPause(it)
                        }
                    )
                    Text(
                        text = stringResource(Res.string.soundInPause),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
