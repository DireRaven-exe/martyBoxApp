package com.jetbrains.kmpapp.feature.commands

import com.jetbrains.kmpapp.domain.models.SongInQueue
import com.jetbrains.kmpapp.domain.models.toSong
import com.jetbrains.kmpapp.ui.screens.queue.QueueViewModel

class QueueCommandHandler(viewModel: QueueViewModel) : BaseCommandHandler(viewModel)  {

    fun moveOn(idFrom: Int, idTo: Int) {
        sendCommand(
            type = 21,
            value = "{\"idFrom\": $idFrom, \"idTo\": $idTo}",
            table = 0
        )
    }



    fun playSoundInPlaylist(id: Int, song: SongInQueue) {
        viewModel.updateCurrentSong(song.toSong())
        sendCommand(
            type = 23,
            value = "$id",
            table = 0
        )
    }

    fun clearPlaylist() {
        sendCommand(
            type = 24,
            value = "true",
            table = 0
        )
        viewModel.updateSongs()
    }

    fun removeSoundFromPlaylist(id: Int) {
        sendCommand(
            type = 25,
            value = "$id",
            table = 0
        )
        //viewModel.updateSongs()
    }

    fun needStates(table: Int) {
        sendCommand(
            type = 26,
            value = "[\"playlist\"]",
            table = table
        )
    }

    fun volume(volume: Float) {
        viewModel.updateVolume(volume)
        sendCommand(
            type = 12,
            value = volume.toString(),
            table = 0
        )
    }

    fun changeAdaptatingTempo(adaptatingTempo: Boolean) {
        sendCommand(
            type = 14,
            value = adaptatingTempo.toString(),
            table = 0
        )
    }

    fun changeSoundInPause(soundInPause: Boolean) {
        sendCommand(
            type = 19,
            value = soundInPause.toString(),
            table = 0
        )
        viewModel.updateSoundInPause(soundInPause)
    }

    fun updateVolume(volume: Float) {
        viewModel.updateVolume(volume)
    }

    fun updateTempo(tempo: Float) {
        viewModel.updateTempo(tempo)
    }

    fun updatePitch(pitch: Int) {
        viewModel.updatePitch(pitch)
    }

    fun updateAutoFullScreen(autoFullScreen: Boolean) {
        viewModel.updateAutoFullScreen(autoFullScreen)
    }

    fun updateSingingAssessment(singingAssessment: Boolean) {
        viewModel.updateSingingAssessment(singingAssessment)
    }

    fun updateSoundInPause(soundInPause: Boolean) {
        viewModel.updateSoundInPause(soundInPause)
    }
}
