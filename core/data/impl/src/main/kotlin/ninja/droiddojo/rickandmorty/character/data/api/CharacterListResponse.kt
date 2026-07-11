package ninja.droiddojo.rickandmorty.character.data.api

import kotlinx.serialization.Serializable

@Serializable
data class CharacterListResponse(
    val results: List<CharacterDto>
)
