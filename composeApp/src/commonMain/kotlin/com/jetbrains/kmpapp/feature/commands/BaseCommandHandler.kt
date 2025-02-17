package com.jetbrains.kmpapp.feature.commands

import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.domain.models.SongInQueue

abstract class BaseCommandHandler(
    protected val viewModel: BaseViewModel
) {
    fun sendCommand(type: Int, value: String, table: Int) {
        viewModel.sendCommand(type, value, table)
    }

    fun clearQueue() {
        sendCommand(type = 24, value = "", table = 0)
    }

    fun removeSoundFromPlaylist(id: Int) {
        sendCommand(
            type = 25,
            value = "$id",
            table = 0
        )
    }

    fun setPitchToFile(id: Int, newPitch: Int) {
        sendCommand(
            type = 22,
            value = "{\"id\": $id, \"newPitch\": $newPitch}",
            table = 0
        )
    }

    fun playTab(tab: String) {
        sendCommand(type = 27, value = "\"$tab\"", table = 0)
    }

    fun requestMedia(song: Song, table: Int, pitch: Int) {
        sendCommand(
            type = 10,
            value = "{\"tab\": \"${song.tab}\", \"id\": ${song.id}, \"pitch\": $pitch}",
            table = table
        )
    }

    fun pause() {
        sendCommand(type = 3, value = "", table = 0)
    }

    fun stop() {
        sendCommand(type = 4, value = "", table = 0)
    }

    fun next() {
        sendCommand(type = 6, value = "", table = 0)
    }

    fun switchPlusMinus() {
        sendCommand(type = 16, value = "", table = 0)
    }

    fun changeTempo(tempo: Float) {
        sendCommand(type = 7, value = tempo.toString(), table = 0)
    }

    fun changePitch(pitch: Int) {
        sendCommand(type = 8, value = pitch.toString(), table = 0)
    }

    fun changeAutoFullScreen(autoFullScreen: Boolean) {
        sendCommand(type = 13, value = autoFullScreen.toString(), table = 0)
    }

    fun changeSingingAssessment(singingAssessment: Boolean) {
        sendCommand(type = 18, value = singingAssessment.toString(), table = 0,)
    }

    fun playAfterPause() {
        sendCommand(type = 11, value = "", table = 0)
    }

    fun moveOn(idFrom: Int, idTo: Int) {
        sendCommand(
            type = 21,
            value = "{\"idFrom\": $idFrom, \"idTo\": $idTo}",
            table = 0
        )
    }

    fun removeSong(song: SongInQueue) {
        viewModel.removeSong(song)
    }

}