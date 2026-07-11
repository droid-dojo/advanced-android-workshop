package ninja.droiddojo.rickandmorty.character.list

import app.cash.turbine.test
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import ninja.droiddojo.rickandmorty.character.data.Character
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertIs

class CharacterListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val rick = character(id = 1, name = "Rick Sanchez")
    private val morty = character(id = 2, name = "Morty Smith")

    @Test
    fun `state is Loading before the first refresh completes`() = runTest {
        val repository = FakeCharacterRepository()
        repository.charactersFromNetwork = listOf(rick)

        val viewModel = CharacterListViewModel(repository)

        viewModel.uiState.test {
            // The virtual clock has not moved yet: init { refresh() } did not run
            assertEquals(CharacterListUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful refresh exposes characters as Success`() = runTest {
        val repository = FakeCharacterRepository()
        repository.charactersFromNetwork = listOf(rick, morty)

        val viewModel = CharacterListViewModel(repository)

        // stateIn(WhileSubscribed) needs at least one collector,
        // otherwise the upstream never starts (Handout 8.6)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        advanceUntilIdle() // run init { refresh() } on the virtual clock

        assertEquals(
            CharacterListUiState.Success(listOf(rick, morty)),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `refresh failure with cached data keeps Success and raises the flag`() = runTest {
        val repository = FakeCharacterRepository(initial = listOf(rick))
        repository.shouldFailRefresh = true

        val viewModel = CharacterListViewModel(repository)

        viewModel.uiState.test {
            advanceUntilIdle()

            val state = assertIs<CharacterListUiState.Success>(expectMostRecentItem())
            assertEquals(listOf(rick), state.characters)
            assertTrue(state.isRefreshFailed)
        }
    }

    @Test
    fun `refresh failure with empty cache surfaces Error`() = runTest {
        val repository = FakeCharacterRepository()
        repository.shouldFailRefresh = true

        val viewModel = CharacterListViewModel(repository)

        viewModel.uiState.test {
            advanceUntilIdle()

            assertTrue(expectMostRecentItem() is CharacterListUiState.Error)
        }
    }

    @Test
    fun `retry after failure clears the flag`() = runTest {
        val repository = FakeCharacterRepository(initial = listOf(rick))
        repository.shouldFailRefresh = true

        val viewModel = CharacterListViewModel(repository)

        viewModel.uiState.test {
            advanceUntilIdle()
            assertTrue(assertIs<CharacterListUiState.Success>(expectMostRecentItem()).isRefreshFailed)

            repository.shouldFailRefresh = false
            repository.charactersFromNetwork = listOf(rick, morty)
            viewModel.refresh()
            advanceUntilIdle()

            val state = assertIs<CharacterListUiState.Success>(expectMostRecentItem())
            assertFalse(state.isRefreshFailed)
            assertEquals(listOf(rick, morty), state.characters)
        }
    }

    @Test
    fun `toggleFavorite is reflected in the exposed state`() = runTest {
        val repository = FakeCharacterRepository(initial = listOf(rick))
        repository.charactersFromNetwork = listOf(rick)

        val viewModel = CharacterListViewModel(repository)

        viewModel.uiState.test {
            advanceUntilIdle()

            viewModel.toggleFavorite(rick.id)
            advanceUntilIdle()

            val state = assertIs<CharacterListUiState.Success>(expectMostRecentItem())
            assertTrue(state.characters.single().isFavorite)
        }
    }

    private fun character(id: Int, name: String) = Character(
        id = id,
        name = name,
        status = "Alive",
        species = "Human",
        gender = "Male",
        origin = null,
        location = null,
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
    )
}
