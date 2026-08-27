package ninja.droiddojo.rickandmorty.character.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ninja.droiddojo.rickandmorty.character.data.db.CharacterDao
import ninja.droiddojo.rickandmorty.character.data.db.CharacterEntity

class FakeCharacterDao(initial: List<CharacterEntity> = emptyList()) : CharacterDao {

    // In-memory "table", keyed by primary key
    private val table = MutableStateFlow(initial.associateBy { it.id })

    override fun observeAll(): Flow<List<CharacterEntity>> =
        table.map { rows -> rows.values.sortedBy { it.id } }

    override fun observeById(id: Int): Flow<CharacterEntity?> =
        table.map { rows -> rows[id] }

    override suspend fun upsertAll(characters: List<CharacterEntity>) {
        table.update { rows -> rows + characters.associateBy { it.id } }
    }

    override suspend fun upsert(character: CharacterEntity) {
        table.update { rows -> rows + (character.id to character) }
    }

    override suspend fun getFavoriteIds(): List<Int> =
        table.value.values.filter { it.isFavorite }.map { it.id }

    override suspend fun toggleFavorite(id: Int) {
        table.update { rows ->
            val row = rows[id] ?: return@update rows
            rows + (id to row.copy(isFavorite = !row.isFavorite))
        }
    }
}
