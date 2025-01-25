package com.jetbrains.kmpapp.data.datasources

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.feature.datastore.QueueRepository
import io.github.aakira.napier.Napier

class QueueRepositoryImpl: QueueRepository {
    private val songs: SnapshotStateList<Song> = SnapshotStateList()
    private val isPlaying: Boolean = false
    private val currentSong: Song? = null

    override fun addSong(newSong: Song) {
        songs.add(newSong)
        Napier.e(tag = "QUEUE_REPOSITORY", message = songs.toString())
    }

    override fun deleteSong(index: Int) {
        songs.removeAt(index)
    }

    override fun deleteSongs() {
        songs.clear()
    }

    override fun editSongPosition(newIndex: Int, oldIndex: Int) {
        val song = songs[oldIndex]
        songs.removeAt(oldIndex)
        songs.add(newIndex, song)
    }

    override fun setPitch(pitch: Int) {
        TODO("Not yet implemented")
    }

    override fun getSongs(): SnapshotStateList<Song> {
        return songs
    }

    override fun setSongs(newSongs: SnapshotStateList<Song>) {
        songs.clear()
        songs.addAll(newSongs)
    }
}