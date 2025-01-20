package com.jetbrains.kmpapp.feature.datastore

import com.jetbrains.kmpapp.domain.models.Song

interface SongsRepository {
    fun setSongs(songs: List<Song>)
    fun getArtists(): List<String>
    fun getAllSongs(): List<Song>
}