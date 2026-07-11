package ninja.droiddojo.rickandmorty.character.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ninja.droiddojo.rickandmorty.PreviewContainer
import ninja.droiddojo.rickandmorty.character.CharacterSampleData

@Composable
fun CharacterListScreen(
    viewModel: CharacterListViewModel = viewModel(),
    onCharacterClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CharacterListContent(
        state = state,
        onFavoriteClick = viewModel::toggleFavorite,
        onCharacterClick = onCharacterClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterListContent(
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
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is CharacterListUiState.Error -> {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = state.message
                    )
                }

                is CharacterListUiState.Success -> {
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

private class CharacterListScreenPreviewParameterProvider :
    CollectionPreviewParameterProvider<CharacterListUiState>(
        listOf(
            CharacterListUiState.Loading,
            CharacterListUiState.Error("Something went wrong"),
            CharacterListUiState.Success(characters = CharacterSampleData.fakeCharacters)
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