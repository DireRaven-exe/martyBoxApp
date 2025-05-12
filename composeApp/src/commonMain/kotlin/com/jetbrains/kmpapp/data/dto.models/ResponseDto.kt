package com.jetbrains.kmpapp.data.dto.models

import androidx.compose.runtime.Immutable
import com.jetbrains.kmpapp.domain.models.ServerData
import com.jetbrains.kmpapp.domain.models.Song
import com.jetbrains.kmpapp.domain.models.SongInQueue
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

@Immutable
@Serializable
data class ResponseDto(
    val type: String,
    val tables: Int,
    val tabs: List<String>,//List<Map<String, List<SongDto>>>
    val version: String
)

@Serializable
data class ResponseSongsForTabDto(
    val last: Boolean,
    @Serializable(with = SongsListDeserializer::class)
    val songs: List<SongDto>,
    val tab: String,
    val isBase: Boolean
)

@Immutable
@Serializable
data class SongDto(
    val artist: String,
    val tab: String = "",
    val id: Int,
    val title: String
)
//fun ResponseDto.toSong(): SnapshotStateList<Song> {
//    val songs = value.flatMap { tabMap ->
//        tabMap.entries.flatMap { (tab, songItems) ->
//            songItems.map {
//                Song(artist = it.artist, id = it.id, title = it.title, tab = tab)
//            }
//        }
//    }
//    return SnapshotStateList<Song>().apply { addAll(songs) }
//}
//
//fun ResponseDto.toSongsInQueue(): SnapshotStateList<SongInQueue> {
//    val songs = value.flatMap { tabMap ->
//        tabMap.entries.flatMap { (tab, songItems) ->
//            songItems.map {
//                SongInQueue(artist = it.artist, id = it.id, title = it.title, tab = tab)
//            }
//        }
//    }
//    return SnapshotStateList<SongInQueue>().apply { addAll(songs) }
//}

fun SongDto.toSong(): Song {
    return Song(artist = artist, id = id, title = title, tab = tab)
}

fun SongDto.toSongInQueue(): SongInQueue {
    return SongInQueue(artist = artist, id = id, title = title, tab = tab)
}

fun ResponseDto.toServerData(): ServerData {
    return ServerData(emptyList(), type, tables, version)
}

@Serializable
data class Command(
    val type: Int,
    val value: String,
    val table: Int
)

object SongsListDeserializer : JsonTransformingSerializer<List<SongDto>>(ListSerializer(SongDto.serializer())) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return if (element is JsonPrimitive && element.isString) {
            try {
                Json.parseToJsonElement(element.content) // Превращаем строку в JSON
            } catch (e: Exception) {
                JsonArray(emptyList()) // Если ошибка — возвращаем пустой массив
            }
        } else {
            element
        }
    }
}
