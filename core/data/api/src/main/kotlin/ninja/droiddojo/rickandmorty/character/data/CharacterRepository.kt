package ninja.droiddojo.rickandmorty.character.data

import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    // READ path: observe the database, the single source of truth
    fun observeCharacters(): Flow<List<Character>>
    fun observeCharacter(id: Int): Flow<Character?>

    // WRITE path: network -> database, never network -> UI
    suspend fun refreshCharacters()
    suspend fun refreshCharacter(id: Int)
    suspend fun toggleFavorite(id: Int)
}
