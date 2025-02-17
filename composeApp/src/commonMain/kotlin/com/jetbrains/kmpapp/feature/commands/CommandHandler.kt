package com.jetbrains.kmpapp.feature.commands

import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.domain.models.SongInQueue
import com.jetbrains.kmpapp.domain.models.toSong
import com.jetbrains.kmpapp.ui.screens.main.MainViewModel

class CommandHandler(viewModel: MainViewModel) : BaseCommandHandler(viewModel) {

    fun playSoundInPlaylist(id: Int, song: SongInQueue) {
        viewModel.updateCurrentSong(song.toSong())
        sendCommand(
            type = 23,
            value = "$id",
            table = 0
        )
        viewModel.updateQueue()
    }

    fun appendMedia(song: Song) {
        sendCommand(
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
        sendCommand(
            type = 2,
            value = "{\"tab\": \"${song.tab}\", \"id\": ${song.id}}",
            table = 0
        )
        viewModel.updateQueue()
        //}
    }

    fun moveSongInQueue(oldIndex: Int, newIndex: Int) {
        viewModel.moveSongInQueue(oldIndex, newIndex)
    }



    fun volume(volume: Float) {
        viewModel.updateVolume(volume)
        sendCommand(
            type = 12,
            value = volume.toString(),
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

    fun updateSongsForTab(tabName: String) {
        viewModel.updateSongsForTab(tabName)
    }

    fun updateQueue() {
        viewModel.updateQueue()
    }

    fun clearPlaylist() {
        viewModel.clearQueue()
        clearQueue()
    }

    fun clearLocalQueue() {
        viewModel.clearQueue()
    }
}
