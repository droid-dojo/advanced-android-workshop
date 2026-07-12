package ninja.droiddojo.rickandmorty.character.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest
import ninja.droiddojo.rickandmorty.PreviewContainer
import ninja.droiddojo.rickandmorty.character.CharacterSampleData

/**
 * Variante B: the official Compose Preview Screenshot Testing tool.
 * Reference images: ./gradlew updateDebugScreenshotTest
 * Validation:       ./gradlew validateDebugScreenshotTest
 */
class CharacterListPreviewScreenshots {

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CharacterListSuccess() {
        PreviewContainer {
            CharacterListContent(
                state = CharacterListUiState.Success(CharacterSampleData.fakeCharacters),
                onFavoriteClick = {},
                onCharacterClick = {},
            )
        }
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CharacterListOfflineBanner() {
        PreviewContainer {
            CharacterListContent(
                state = CharacterListUiState.Success(
                    characters = CharacterSampleData.fakeCharacters,
                    isRefreshFailed = true,
                ),
                onFavoriteClick = {},
                onCharacterClick = {},
            )
        }
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CharacterItemFavorite() {
        PreviewContainer {
            CharacterItem(
                character = CharacterSampleData.fakeCharacters.first().copy(isFavorite = true),
                onFavoriteClick = {},
                onItemClick = {},
            )
        }
    }
}
