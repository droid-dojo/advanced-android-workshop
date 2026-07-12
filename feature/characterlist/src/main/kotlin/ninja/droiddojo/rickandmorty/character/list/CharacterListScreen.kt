package ninja.droiddojo.rickandmorty.character.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ninja.droiddojo.rickandmorty.PreviewContainer
import ninja.droiddojo.rickandmorty.analytics.LocalAnalyticsTracker
import ninja.droiddojo.rickandmorty.analytics.TrackScreen
import ninja.droiddojo.rickandmorty.character.CharacterSampleData

@Composable
fun CharacterListScreen(
    viewModel: CharacterListViewModel = hiltViewModel(),
    onCharacterClick: (Int) -> Unit
) {
    TrackScreen("character_list")

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val tracker = LocalAnalyticsTracker.current

    CharacterListContent(
        state = state,
        onFavoriteClick = { id ->
            // User event, captured at the point of interaction - not in the ViewModel
            tracker.trackEvent("toggle_favorite", mapOf("character_id" to id.toString()))
            viewModel.toggleFavorite(id)
        },
        onCharacterClick = onCharacterClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CharacterListContent(
    state: CharacterListUiState,
    onFavoriteClick: (Int) -> Unit,
    onCharacterClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Rick & Morty Guide") })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state) {
                is CharacterListUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("loading_indicator")
                    )
                }

                is CharacterListUiState.Error -> {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = state.message
                    )
                }

                is CharacterListUiState.Success -> {
                    Column {
                        if (state.isRefreshFailed) {
                            OfflineBanner()
                        }
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.characters) { character ->
                                CharacterItem(
                                    character = character,
                                    onFavoriteClick = { onFavoriteClick(character.id) },
                                    onItemClick = { onCharacterClick(character.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OfflineBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Text(
                text = "Offline — Daten sind möglicherweise nicht aktuell",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

private class CharacterListScreenPreviewParameterProvider :
    CollectionPreviewParameterProvider<CharacterListUiState>(
        listOf(
            CharacterListUiState.Loading,
            CharacterListUiState.Error("Something went wrong"),
            CharacterListUiState.Success(characters = CharacterSampleData.fakeCharacters),
            CharacterListUiState.Success(
                characters = CharacterSampleData.fakeCharacters,
                isRefreshFailed = true,
            ),
        )
    )

@PreviewLightDark
@Composable
private fun Preview(
    @PreviewParameter(CharacterListScreenPreviewParameterProvider::class) state: CharacterListUiState
) {
    PreviewContainer {
        CharacterListContent(
            state = state,
            onFavoriteClick = {},
            onCharacterClick = {},
        )
    }
}