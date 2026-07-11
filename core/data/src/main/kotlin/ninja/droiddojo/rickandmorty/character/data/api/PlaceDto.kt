package ninja.droiddojo.rickandmorty.character.data.api

import kotlinx.serialization.Serializable
import ninja.droiddojo.rickandmorty.character.data.Place

@Serializable
data class PlaceDto(
    val name: String,
    val url: String
)

fun PlaceDto.toDomain(): Place? {
    if (url.isBlank() || name == "unknown") return null

    val id = url.removePrefix("https://rickandmortyapi.com/api/location/").toIntOrNull() ?: return null
    return Place(id = id, name = name)
}
