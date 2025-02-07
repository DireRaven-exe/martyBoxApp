package com.jetbrains.kmpapp.data.dto.models

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.jetbrains.kmpapp.domain.models.ServerData
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.domain.models.SongInQueue
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ResponseDto(
    val type: String,
    val tables: Int,
    val value: List<Map<String, List<SongsDto>>>
)

@Immutable
@Serializable
data class SongsDto(
    val artist: String,
    val tab: String = "",
    val id: Int,
    val title: String
)
fun ResponseDto.toSongs(): SnapshotStateList<Song> {
    val songs = value.flatMap { tabMap ->
        tabMap.entries.flatMap { (tab, songItems) ->
            songItems.map {
                Song(artist = it.artist, id = it.id, title = it.title, tab = tab)
            }
        }
    }
    return SnapshotStateList<Song>().apply { addAll(songs) }
}

fun ResponseDto.toSongsInQueue(): SnapshotStateList<SongInQueue> {
    val songs = value.flatMap { tabMap ->
        tabMap.entries.flatMap { (tab, songItems) ->
            songItems.map {
                SongInQueue(artist = it.artist, id = it.id, title = it.title, tab = tab)
            }
        }
    }
    return SnapshotStateList<SongInQueue>().apply { addAll(songs) }
}

fun SongsDto.toSongs(): Song {
    return Song(artist = artist, id = id, title = title, tab = tab)
}

fun SongsDto.toSongInQueue(): SongInQueue {
    return SongInQueue(artist = artist, id = id, title = title, tab = tab)
}

fun ResponseDto.toServerData(): ServerData {
    return ServerData(type, tables)
}

@Serializable
data class Command(
    val type: Int,
    val value: String,
    val table: Int
)

