package ninja.droiddojo.rickandmorty.character.data.api

import kotlinx.serialization.Serializable
import ninja.droiddojo.rickandmorty.character.data.Character

@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val image: String,
    val species: String,
    val gender: String,
    val origin: PlaceDto,
    val location: PlaceDto,
    val episode: List<String>
)

fun CharacterDto.toDomain(): Character = Character(
    id = id,
    name = name,
    status = status,
    imageUrl = image,
    species = species,
    gender = gender,
    origin = origin.toDomain(),
    location = location.toDomain()
)
