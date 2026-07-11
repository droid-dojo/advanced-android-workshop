package ninja.droiddojo.rickandmorty.character.data

import java.io.IOException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import ninja.droiddojo.rickandmorty.character.data.api.CharacterDto
import ninja.droiddojo.rickandmorty.character.data.api.PlaceDto
import ninja.droiddojo.rickandmorty.character.data.db.CharacterEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterRepositoryTest {

    private val rickDto = characterDto(id = 1, name = "Rick Sanchez")
    private val mortyDto = characterDto(id = 2, name = "Morty Smith")

    @Test
    fun `refreshCharacters writes api data into the database`() = runTest {
        val api = FakeRickAndMortyApi(characters = listOf(rickDto, mortyDto))
        val dao = FakeCharacterDao()
        val repository = CharacterRepository(api, dao)

        repository.refreshCharacters()

        val characters = repository.observeCharacters().first()
        assertEquals(listOf("Rick Sanchez", "Morty Smith"), characters.map { it.name })
    }

    @Test
    fun `failed refresh keeps cached data available`() = runTest {
        val api = FakeRickAndMortyApi(failing = true)
        val dao = FakeCharacterDao(initial = listOf(characterEntity(id = 1, name = "Rick Sanchez")))
        val repository = CharacterRepository(api, dao)

        val error = runCatching { repository.refreshCharacters() }.exceptionOrNull()
        assertTrue(error is IOException)

        val characters = repository.observeCharacters().first()
        assertEquals(listOf("Rick Sanchez"), characters.map { it.name })
    }

    @Test
    fun `toggleFavorite persists the favorite state`() = runTest {
        val dao = FakeCharacterDao(initial = listOf(characterEntity(id = 1, name = "Rick Sanchez")))
        val repository = CharacterRepository(FakeRickAndMortyApi(), dao)

        repository.toggleFavorite(1)

        assertTrue(repository.observeCharacter(1).first()!!.isFavorite)

        repository.toggleFavorite(1)

        assertFalse(repository.observeCharacter(1).first()!!.isFavorite)
    }

    @Test
    fun `refresh does not overwrite local favorites`() = runTest {
        val api = FakeRickAndMortyApi(characters = listOf(rickDto, mortyDto))
        val dao = FakeCharacterDao()
        val repository = CharacterRepository(api, dao)

        repository.refreshCharacters()
        repository.toggleFavorite(1)

        repository.refreshCharacters()

        val characters = repository.observeCharacters().first()
        assertTrue(characters.first { it.id == 1 }.isFavorite)
        assertFalse(characters.first { it.id == 2 }.isFavorite)
    }

    @Test
    fun `refreshCharacter updates a single row`() = runTest {
        val renamedRick = characterDto(id = 1, name = "Pickle Rick")
        val api = FakeRickAndMortyApi(characters = listOf(renamedRick))
        val dao = FakeCharacterDao(initial = listOf(characterEntity(id = 1, name = "Rick Sanchez")))
        val repository = CharacterRepository(api, dao)

        repository.refreshCharacter(1)

        assertEquals("Pickle Rick", repository.observeCharacter(1).first()?.name)
    }

    private fun characterDto(id: Int, name: String) = CharacterDto(
        id = id,
        name = name,
        status = "Alive",
        image = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
        species = "Human",
        gender = "Male",
        origin = PlaceDto(name = "Earth (C-137)", url = "https://rickandmortyapi.com/api/location/1"),
        location = PlaceDto(name = "Citadel of Ricks", url = "https://rickandmortyapi.com/api/location/3"),
        episode = emptyList(),
    )

    private fun characterEntity(id: Int, name: String, isFavorite: Boolean = false) = CharacterEntity(
        id = id,
        name = name,
        status = "Alive",
        species = "Human",
        gender = "Male",
        originId = 1,
        originName = "Earth (C-137)",
        locationId = 3,
        locationName = "Citadel of Ricks",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
        isFavorite = isFavorite,
    )
}
