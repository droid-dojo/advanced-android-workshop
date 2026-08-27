package ninja.droiddojo.rickandmorty.character.list

import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ninja.droiddojo.rickandmorty.character.data.Character
import ninja.droiddojo.rickandmorty.character.data.CharacterRepository

class FakeCharacterRepository(
    initial: List<Character> = emptyList(),
) : CharacterRepository {

    // The fake "database" - a real, reactive flow
    private val characters = MutableStateFlow(initial)

    // The fake "network" - configurable per test
    var charactersFromNetwork: List<Character> = emptyList()
    var shouldFailRefresh = false

    override fun observeCharacters(): Flow<List<Character>> = characters

    override fun observeCharacter(id: Int): Flow<Character?> =
        characters.map { list -> list.find { it.id == id } }

    override suspend fun refreshCharacters() {
        if (shouldFailRefresh) throw IOException("No network")
        characters.value = charactersFromNetwork
    }

    override suspend fun refreshCharacter(id: Int) {
        if (shouldFailRefresh) throw IOException("No network")
        val fresh = charactersFromNetwork.find { it.id == id } ?: return
        characters.update { list -> list.filterNot { it.id == id } + fresh }
    }

    override suspend fun toggleFavorite(id: Int) {
        characters.update { list ->
            list.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }
}
