package com.jetbrains.kmpapp.data.datasources

import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.feature.datastore.SongsRepository

class SongsRepositoryImpl: SongsRepository {

    private var songs: List<Song> = emptyList() // Здесь храним все песни

    // Метод для получения всех песен
    override fun getAllSongs(): List<Song> {
        return songs
    }

    // Метод для получения списка артистов (из песен)
    override fun getArtists(): List<String> {
        return songs.map { it.artist }.distinct()
    }

    // Метод для установки песен (например, из API или базы данных)
    override fun setSongs(songs: List<Song>) {
        this.songs = songs
    }
}