package ninja.droiddojo.rickandmorty.character.data

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ninja.droiddojo.rickandmorty.analytics.AppLogger
import ninja.droiddojo.rickandmorty.character.data.api.RickAndMortyApi
import ninja.droiddojo.rickandmorty.character.data.db.CharacterDao
import ninja.droiddojo.rickandmorty.character.data.db.toDomain
import ninja.droiddojo.rickandmorty.character.data.db.toEntity

@Singleton
class OfflineFirstCharacterRepository @Inject constructor(
    private val api: RickAndMortyApi,
    private val dao: CharacterDao,
    private val logger: AppLogger,
) : CharacterRepository {

    override fun observeCharacters(): Flow<List<Character>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeCharacter(id: Int): Flow<Character?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun refreshCharacters() {
        try {
            // The API knows nothing about favorites: keep the local flags alive
            val favoriteIds = dao.getFavoriteIds().toSet()
            val entities = api.getCharacters().results.map { dto ->
                dto.toEntity(isFavorite = dto.id in favoriteIds)
            }
            dao.upsertAll(entities)
            logger.debug(TAG, "Cached ${entities.size} characters")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Log-and-rethrow: record the technical detail here,
            // the caller still decides what the failure means
            logger.error(TAG, "Refreshing character list failed", e)
            throw e
        }
    }

    override suspend fun refreshCharacter(id: Int) {
        try {
            val favoriteIds = dao.getFavoriteIds().toSet()
            dao.upsert(api.getCharacter(id).toEntity(isFavorite = id in favoriteIds))
            logger.debug(TAG, "Refreshed character $id")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.error(TAG, "Refreshing character $id failed", e)
            throw e
        }
    }

    override suspend fun toggleFavorite(id: Int) {
        dao.toggleFavorite(id)
    }

    companion object {
        private const val TAG = "OfflineFirstCharacterRepository"
    }
}
