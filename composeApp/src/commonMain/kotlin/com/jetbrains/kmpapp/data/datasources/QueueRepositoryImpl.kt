package com.jetbrains.kmpapp.data.datasources

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.domain.models.SongInQueue
import com.jetbrains.kmpapp.domain.models.toSongInQueue
import com.jetbrains.kmpapp.feature.datastore.QueueRepository

class QueueRepositoryImpl : QueueRepository {
    private val _songs = mutableStateOf<MutableList<SongInQueue>>(mutableListOf())
    val songs: State<List<SongInQueue>> get() = _songs

    override fun addSong(newSong: Song) {
        _songs.value += newSong.toSongInQueue()
    }

    override fun deleteSong(index: Int) {
        _songs.value = _songs.value.toMutableList().apply { removeAt(index) }
    }

    override fun deleteSong(song: SongInQueue) {
        _songs.value = _songs.value.toMutableList().apply { remove(song) }
    }

    override fun deleteSongs() {
        TODO("Not yet implemented")
    }

    override fun editSongPosition(newIndex: Int, oldIndex: Int) {
        val updatedSongs = _songs.value.toMutableList()
        val song = updatedSongs.removeAt(oldIndex)
        updatedSongs.add(newIndex, song)
        _songs.value = updatedSongs
    }

    override fun setPitch(pitch: Int) {
        TODO("Not yet implemented")
    }

    override fun getSongs(): MutableList<SongInQueue> {
        return _songs.value
    }

    override fun setSongs(newSongs: MutableList<Song>) {
        _songs.value = newSongs.map { it.toSongInQueue() }.toMutableList()
    }
}