package com.jetbrains.kmpapp.feature.datastore

import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.domain.models.SongInQueue

interface QueueRepository {
    fun addSong(newSong: Song)

    fun deleteSong(index: Int)

    fun deleteSong(song: SongInQueue)

    fun deleteSongs()

    fun editSongPosition(newIndex: Int, oldIndex: Int)

    fun setPitch(pitch: Int)

    fun getSongs(): MutableList<SongInQueue>

    fun setSongs(newSongs: MutableList<Song>)
}
