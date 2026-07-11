package ninja.droiddojo.rickandmorty.character.data.api

import android.util.Log
import kotlinx.serialization.Serializable
import ninja.droiddojo.rickandmorty.character.data.Place

@Serializable
data class PlaceDto(
    val name: String,
    val url: String
)

fun PlaceDto.toDomain(): Place? {
    if (url.isBlank() || name == "unknown") return null

    return try {
        Place(
            id = url.removePrefix("https://rickandmortyapi.com/api/location/").toInt(),
            name = name
        )
    } catch (e: NumberFormatException) {
        Log.e("PlaceDto", "Invalid URL format: $url for location: $name", e)
        null
    }
}
