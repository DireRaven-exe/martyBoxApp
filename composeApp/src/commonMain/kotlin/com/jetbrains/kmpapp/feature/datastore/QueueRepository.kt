package com.jetbrains.kmpapp.feature.datastore

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.jetbrains.kmpapp.domain.models.Song

interface QueueRepository {
    fun addSong(newSong: Song)

    fun deleteSong(index: Int)

    fun deleteSongs()

    fun editSongPosition(newIndex: Int, oldIndex: Int)

    fun setPitch(pitch: Int)

    fun getSongs(): SnapshotStateList<Song>

    fun setSongs(newSongs: SnapshotStateList<Song>)
}
