package com.jetbrains.kmpapp.domain.models

import androidx.compose.runtime.Immutable
import io.github.aakira.napier.Napier

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
    val key: String = generateUniqueKey(id),
    val title: String
)

internal val generatedKeys = mutableSetOf<String>()

fun generateUniqueKey(id: Int): String {
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    var key: String

    do {
        // Генерация уникального ключа на основе id и случайной строки
        key = "$id-" + (1..16)
            .map { allowedChars.random() }
            .joinToString("")
    } while (generatedKeys.contains(key))  // Проверяем, существует ли такой ключ

    generatedKeys.add(key)  // Добавляем ключ в множество сгенерированных
    Napier.e(tag = "GENERATEDKEY", message = "Generated key: $key")
    Napier.e(tag = "GENERATEDKEY", message = "-------------------------------------------------")
    return key
}

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

