package com.jetbrains.kmpapp.feature.messages

import androidx.compose.runtime.Immutable
import com.jetbrains.kmpapp.feature.messages.states.AdaptatingTempoMessage
import com.jetbrains.kmpapp.feature.messages.states.AutoFullScreenMessage
import com.jetbrains.kmpapp.feature.messages.states.HasPlusMessage
import com.jetbrains.kmpapp.feature.messages.states.NothingMessage
import com.jetbrains.kmpapp.feature.messages.states.PitchMessage
import com.jetbrains.kmpapp.feature.messages.states.PlayingMessage
import com.jetbrains.kmpapp.feature.messages.states.PlaylistMessage
import com.jetbrains.kmpapp.feature.messages.states.SingingAssessmentMessage
import com.jetbrains.kmpapp.feature.messages.states.SongMessage
import com.jetbrains.kmpapp.feature.messages.states.SoundInPauseMessage
import com.jetbrains.kmpapp.feature.messages.states.TempoMessage
import com.jetbrains.kmpapp.feature.messages.states.VolumeMessage

@Immutable
object States {
    private val keyMessageMap: Map<String, StateMessage> = mapOf(
        "Pitch" to PitchMessage(),
        "Tempo" to TempoMessage(),
        "Volume" to VolumeMessage(),
        "Playing" to PlayingMessage(),
        "Song" to SongMessage(),
        "AutoFullScreen" to AutoFullScreenMessage(),
        "AdaptatingTempo" to AdaptatingTempoMessage(),
        "HasPlus" to HasPlusMessage(),
        "SingingAssessment" to SingingAssessmentMessage(),
        "SoundInPause" to SoundInPauseMessage(),
        "playlist" to PlaylistMessage(),
    )

    fun getMessage(key: String): StateMessage {
        return keyMessageMap[key] ?: NothingMessage()
    }
}