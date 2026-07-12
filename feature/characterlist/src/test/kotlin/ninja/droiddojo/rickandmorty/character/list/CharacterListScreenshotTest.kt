package ninja.droiddojo.rickandmorty.character.list

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import ninja.droiddojo.rickandmorty.character.CharacterSampleData
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class CharacterListScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun characterList_success() {
        setListContent(CharacterListUiState.Success(CharacterSampleData.fakeCharacters))

        capture("character_list_success.png")
    }

    @Test
    fun characterList_success_dark() {
        setListContent(
            CharacterListUiState.Success(CharacterSampleData.fakeCharacters),
            darkTheme = true,
        )

        capture("character_list_success_dark.png")
    }

    @Test
    fun characterList_offlineBanner() {
        setListContent(
            CharacterListUiState.Success(
                characters = CharacterSampleData.fakeCharacters,
                isRefreshFailed = true,
            )
        )

        capture("character_list_offline_banner.png")
    }

    @Test
    fun characterList_loading() {
        setListContent(CharacterListUiState.Loading)

        capture("character_list_loading.png")
    }

    @Test
    fun characterList_error() {
        setListContent(CharacterListUiState.Error("Keine Verbindung"))

        capture("character_list_error.png")
    }

    @Test
    fun characterItem_favorite() {
        composeTestRule.setContent {
            ScreenshotContainer {
                CharacterItem(
                    character = CharacterSampleData.fakeCharacters.first().copy(isFavorite = true),
                    onFavoriteClick = {},
                    onItemClick = {},
                )
            }
        }

        capture("character_item_favorite.png")
    }

    @Test
    fun characterItem_notFavorite() {
        composeTestRule.setContent {
            ScreenshotContainer {
                CharacterItem(
                    character = CharacterSampleData.fakeCharacters.first(),
                    onFavoriteClick = {},
                    onItemClick = {},
                )
            }
        }

        capture("character_item_not_favorite.png")
    }

    private fun setListContent(state: CharacterListUiState, darkTheme: Boolean = false) {
        composeTestRule.setContent {
            ScreenshotContainer(darkTheme = darkTheme) {
                CharacterListContent(
                    state = state,
                    onFavoriteClick = {},
                    onCharacterClick = {},
                )
            }
        }
    }

    private fun capture(fileName: String) {
        composeTestRule.onRoot().captureRoboImage("src/test/screenshots/$fileName")
    }
}
