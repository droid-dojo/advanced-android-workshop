package ninja.droiddojo.rickandmorty.character.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import ninja.droiddojo.rickandmorty.character.data.Character
import ninja.droiddojo.rickandmorty.character.data.Place
import ninja.droiddojo.rickandmorty.character.data.api.CharacterDto
import ninja.droiddojo.rickandmorty.character.data.api.toDomain

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val originId: Int?,
    val originName: String?,
    val locationId: Int?,
    val locationName: String?,
    val imageUrl: String,
    val isFavorite: Boolean,
)

fun CharacterEntity.toDomain(): Character = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    gender = gender,
    origin = toPlace(originId, originName),
    location = toPlace(locationId, locationName),
    imageUrl = imageUrl,
    isFavorite = isFavorite,
)

fun CharacterDto.toEntity(isFavorite: Boolean): CharacterEntity {
    val origin = origin.toDomain()
    val location = location.toDomain()
    return CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        gender = gender,
        originId = origin?.id,
        originName = origin?.name,
        locationId = location?.id,
        locationName = location?.name,
        imageUrl = image,
        isFavorite = isFavorite,
    )
}

private fun toPlace(id: Int?, name: String?): Place? =
    if (id != null && name != null) Place(id = id, name = name) else null
