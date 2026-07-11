package ninja.droiddojo.rickandmorty.character.data

import javax.inject.Inject
import javax.inject.Singleton
import ninja.droiddojo.rickandmorty.character.data.api.RickAndMortyApi
import ninja.droiddojo.rickandmorty.character.data.api.toDomain

@Singleton
class CharacterRepository @Inject constructor(private val api: RickAndMortyApi) {
    suspend fun getCharacters(): List<Character> {
        return api.getCharacters().results.map { it.toDomain() }
    }

    suspend fun getCharacter(id: Int): Character {
        return api.getCharacter(id).toDomain()
    }
}
