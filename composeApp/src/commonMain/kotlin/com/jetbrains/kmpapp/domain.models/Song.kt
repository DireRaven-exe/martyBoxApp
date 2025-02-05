package com.jetbrains.kmpapp.domain.models

import androidx.compose.runtime.Immutable

@Immutable
data class Song(
    val artist: String,
    val tab: String,
    val id: Int,
    val title: String
)

@Immutable
data class SongInQueue(
    val artist: String,
    val tab: String,
    val id: Int,
    val key: String = generateUniqueKey(),
    val title: String
)

fun Song.toSongInQueue(): SongInQueue {
    return SongInQueue(
        artist = this.artist,
        tab = this.tab,
        id = this.id,
        title = this.title
    )
}

fun SongInQueue.toSong() : Song {
    return Song(
        artist = this.artist,
        tab = this.tab,
        id = this.id,
        title = this.title
    )
}

fun MutableList<SongInQueue>.toSong(): MutableList<Song> {
    val songList = mutableListOf<Song>()
    for (songInQueue in this) {
        songList.add(songInQueue.toSong())
    }
    return songList
}

fun generateUniqueKey(): String {
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..16)
        .map { allowedChars.random() }
        .joinToString("")
}