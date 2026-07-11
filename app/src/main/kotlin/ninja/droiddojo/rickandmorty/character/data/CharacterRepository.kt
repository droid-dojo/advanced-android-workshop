package ninja.droiddojo.rickandmorty.character.data

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ninja.droiddojo.rickandmorty.character.data.api.RickAndMortyApi
import ninja.droiddojo.rickandmorty.character.data.db.CharacterDao
import ninja.droiddojo.rickandmorty.character.data.db.toDomain
import ninja.droiddojo.rickandmorty.character.data.db.toEntity

@Singleton
class CharacterRepository @Inject constructor(
    private val api: RickAndMortyApi,
    private val dao: CharacterDao,
) {
    // READ path: observe the database, the single source of truth
    fun observeCharacters(): Flow<List<Character>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    fun observeCharacter(id: Int): Flow<Character?> =
        dao.observeById(id).map { it?.toDomain() }

    // WRITE path: network -> database, never network -> UI
    suspend fun refreshCharacters() {
        // The API knows nothing about favorites: keep the local flags alive
        val favoriteIds = dao.getFavoriteIds().toSet()
        val entities = api.getCharacters().results.map { dto ->
            dto.toEntity(isFavorite = dto.id in favoriteIds)
        }
        dao.upsertAll(entities)
    }

    suspend fun refreshCharacter(id: Int) {
        val favoriteIds = dao.getFavoriteIds().toSet()
        dao.upsert(api.getCharacter(id).toEntity(isFavorite = id in favoriteIds))
    }

    suspend fun toggleFavorite(id: Int) {
        dao.toggleFavorite(id)
    }
}
