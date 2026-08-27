package ninja.droiddojo.rickandmorty.character.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters ORDER BY id")
    fun observeAll(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun observeById(id: Int): Flow<CharacterEntity?>

    @Upsert
    suspend fun upsertAll(characters: List<CharacterEntity>)

    @Upsert
    suspend fun upsert(character: CharacterEntity)

    @Query("SELECT id FROM characters WHERE isFavorite = 1")
    suspend fun getFavoriteIds(): List<Int>

    @Query("UPDATE characters SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Int)
}
