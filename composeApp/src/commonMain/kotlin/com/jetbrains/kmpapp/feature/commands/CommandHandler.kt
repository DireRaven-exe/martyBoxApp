package com.jetbrains.kmpapp.feature.commands

import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.ui.screens.main.MainViewModel

class CommandHandler(private val viewModel: MainViewModel) {
    fun appendMedia(song: Song) {
        viewModel.sendCommandToServer(
            type = 9,
            value = "{\"tab\": \"${song.tab}\", \"id\": ${song.id}}",
            table = song.id
        )
        viewModel.addSongToQueue(song)
    }

    fun play(song: Song) {
//        if (viewModel.mainUiState.value.currentSong == song) {
//            viewModel.updateCurrentSong(null)
//        } else {
            viewModel.updateCurrentSong(song)
            viewModel.sendCommandToServer(
                type = 2,
                value = "{\"tab\": \"${song.tab}\", \"id\": ${song.id}}",
                table = 0
            )
        //}
    }

    fun requestMedia(song: Song, table: Int) {
        viewModel.sendCommandToServer(
            type = 10,
            value = "{\"tab\": \"${song.tab}\", \"id\": ${song.id}}",
            table = table
        )
    }

    fun pause() {
        viewModel.sendCommandToServer(
            type = 3,
            value = "",
            table = 0
        )
    }

    fun stop() {
        viewModel.sendCommandToServer(
            type = 4,
            value = "",
            table = 0
        )
    }

    fun next() {
        viewModel.sendCommandToServer(
            type = 6,
            value = "",
            table = 0
        )
    }

    fun switchPlusMinus() {
        viewModel.sendCommandToServer(
            type = 16,
            value = "",
            table = 0
        )
    }

    fun volume(volume: Float) {
        viewModel.updateVolume(volume)
        viewModel.sendCommandToServer(
            type = 12,
            value = volume.toString(),
            table = 0
        )
    }

    fun changeTempo(tempo: Float) {
        viewModel.sendCommandToServer(
            type = 7,
            value = tempo.toString(),
            table = 0
        )
    }

    fun changePitch(pitch: Int) {
        viewModel.sendCommandToServer(
            type = 8,
            value = pitch.toString(),
            table = 0
        )
    }

    fun changeAutoFullScreen(autoFullScreen: Boolean) {
        viewModel.sendCommandToServer(
            type = 13,
            value = autoFullScreen.toString(),
            table = 0
        )
    }

    fun changeAdaptatingTempo(adaptatingTempo: Boolean) {
        viewModel.sendCommandToServer(
            type = 14,
            value = adaptatingTempo.toString(),
            table = 0
        )
    }

    fun changeSingingAssessment(singingAssessment: Boolean) {
        viewModel.sendCommandToServer(
            type = 18,
            value = singingAssessment.toString(),
            table = 0,
        )
    }

    fun changeSoundInPause(soundInPause: Boolean) {
        viewModel.sendCommandToServer(
            type = 19,
            value = soundInPause.toString(),
            table = 0
        )
        viewModel.updateSoundInPause(soundInPause)
    }

    fun playAfterPause() {
        viewModel.sendCommandToServer(
            type = 11,
            value = "",
            table = 0
        )
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

    fun updateAdaptatingTempo(adaptatingTempo: Boolean) {
        viewModel.updateAdaptatingTempo(adaptatingTempo)
    }

    fun updateSingingAssessment(singingAssessment: Boolean) {
        viewModel.updateSingingAssessment(singingAssessment)
    }

    fun updateSoundInPause(soundInPause: Boolean) {
        viewModel.updateSoundInPause(soundInPause)
    }
}
