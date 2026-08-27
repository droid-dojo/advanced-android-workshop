package ninja.droiddojo.rickandmorty.character.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ninja.droiddojo.rickandmorty.character.data.Character
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class CharacterListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val rick = character(id = 1, name = "Rick Sanchez")
    private val morty = character(id = 2, name = "Morty Smith", isFavorite = true)

    @Test
    fun `loading state shows the loading indicator`() {
        setContent(CharacterListUiState.Loading)

        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun `success state shows the characters`() {
        setContent(CharacterListUiState.Success(listOf(rick, morty)))

        composeTestRule.onNodeWithText("Rick Sanchez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Morty Smith").assertIsDisplayed()
    }

    @Test
    fun `error state shows the message`() {
        setContent(CharacterListUiState.Error("Keine Verbindung"))

        composeTestRule.onNodeWithText("Keine Verbindung").assertIsDisplayed()
    }

    @Test
    fun `offline banner is shown only when the refresh failed`() {
        setContent(CharacterListUiState.Success(listOf(rick), isRefreshFailed = true))

        composeTestRule
            .onNodeWithText("Offline — Daten sind möglicherweise nicht aktuell")
            .assertIsDisplayed()
    }

    @Test
    fun `offline banner is hidden when the refresh succeeded`() {
        setContent(CharacterListUiState.Success(listOf(rick)))

        composeTestRule
            .onNodeWithText("Offline — Daten sind möglicherweise nicht aktuell")
            .assertDoesNotExist()
    }

    @Test
    fun `clicking a character emits its id`() {
        var clickedId: Int? = null
        setContent(
            CharacterListUiState.Success(listOf(rick, morty)),
            onCharacterClick = { clickedId = it },
        )

        composeTestRule.onNodeWithText("Morty Smith").performClick()

        assertEquals(morty.id, clickedId)
    }

    @Test
    fun `clicking the favorite icon emits its id`() {
        var favoriteId: Int? = null
        setContent(
            CharacterListUiState.Success(listOf(rick)),
            onFavoriteClick = { favoriteId = it },
        )

        // rick is not a favorite yet -> the icon offers "add"
        composeTestRule.onNodeWithContentDescription("Add to favorites").performClick()

        assertEquals(rick.id, favoriteId)
    }

    @Test
    fun `favorite icon semantics reflect the favorite state`() {
        setContent(CharacterListUiState.Success(listOf(morty)))

        composeTestRule.onNodeWithContentDescription("Remove from favorites").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add to favorites").assertDoesNotExist()
    }

    private fun setContent(
        state: CharacterListUiState,
        onFavoriteClick: (Int) -> Unit = {},
        onCharacterClick: (Int) -> Unit = {},
    ) {
        composeTestRule.setContent {
            CharacterListContent(
                state = state,
                onFavoriteClick = onFavoriteClick,
                onCharacterClick = onCharacterClick,
            )
        }
    }

    private fun character(id: Int, name: String, isFavorite: Boolean = false) = Character(
        id = id,
        name = name,
        status = "Alive",
        species = "Human",
        gender = "Male",
        origin = null,
        location = null,
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
        isFavorite = isFavorite,
    )
}
