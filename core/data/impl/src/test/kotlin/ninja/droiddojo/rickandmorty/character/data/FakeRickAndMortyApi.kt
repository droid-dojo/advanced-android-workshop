package ninja.droiddojo.rickandmorty.character.data

import java.io.IOException
import ninja.droiddojo.rickandmorty.character.data.api.CharacterDto
import ninja.droiddojo.rickandmorty.character.data.api.CharacterListResponse
import ninja.droiddojo.rickandmorty.character.data.api.RickAndMortyApi

class FakeRickAndMortyApi(
    var characters: List<CharacterDto> = emptyList(),
    var failing: Boolean = false,
) : RickAndMortyApi {

    override suspend fun getCharacters(): CharacterListResponse {
        if (failing) throw IOException("No network")
        return CharacterListResponse(results = characters)
    }

    override suspend fun getCharacter(id: Int): CharacterDto {
        if (failing) throw IOException("No network")
        return characters.first { it.id == id }
    }
}
