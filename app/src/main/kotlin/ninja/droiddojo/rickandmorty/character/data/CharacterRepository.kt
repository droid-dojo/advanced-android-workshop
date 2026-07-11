package ninja.droiddojo.rickandmorty.character.data

import ninja.droiddojo.rickandmorty.character.data.api.RickAndMortyApi
import ninja.droiddojo.rickandmorty.character.data.api.toDomain

class CharacterRepository(private val api: RickAndMortyApi) {
    suspend fun getCharacters(): List<Character> {
        return api.getCharacters().results.map { it.toDomain() }
    }

    suspend fun getCharacter(id: Int): Character {
        return api.getCharacter(id).toDomain()
    }
}
