package com.jetbrains.kmpapp.feature.commands

import com.jetbrains.kmpapp.domain.models.Song

interface CommandHandlerModel {
    fun sendCommandToServer(type: Int, value: String, table: Int)

    fun appendMedia(song: Song)
    fun play(song: Song)
    fun requestMedia(song: Song)
    fun pause()
    fun stop()
    fun next()
    fun playAfterPause()
    fun switchPlusMinus()
    fun volume(volume: Float)
    fun changeTempo(tempo: Float)
    fun changePitch(pitch: Int)
    fun changeAutoFullScreen(autoFullScreen: Boolean)
    fun changeAdaptatingTempo(adaptatingTempo: Boolean)
    fun changeSingingAssessment(singingAssessment: Boolean)
    fun changeSoundInPause(soundInPause: Boolean)

    fun updateVolume(volume: Float)
    fun updateTempo(tempo: Float)
    fun updatePitch(pitch: Int)
    fun updateAutoFullScreen(autoFullScreen: Boolean)
    fun updateAdaptatingTempo(adaptatingTempo: Boolean)
    fun updateSingingAssessment(singingAssessment: Boolean)
    fun updateSoundInPause(soundInPause: Boolean)
    fun updateCurrentSong(song: Song?)
    fun addSongToQueue(song: Song)
}