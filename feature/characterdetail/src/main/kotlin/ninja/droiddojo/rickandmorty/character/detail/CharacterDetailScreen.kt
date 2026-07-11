package ninja.droiddojo.rickandmorty.character.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ninja.droiddojo.rickandmorty.PreviewContainer
import ninja.droiddojo.rickandmorty.feature.characterdetail.R
import ninja.droiddojo.rickandmorty.character.CharacterSampleData

@Composable
fun CharacterDetailScreen(
    viewModel: CharacterDetailViewModel,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CharacterDetailContent(
        state = state,
        onNavigateBackClick = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterDetailContent(state: CharacterDetailUiState, onNavigateBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back to the List"
                        )
                    }
                },
                title = {
                    Text(
                        text = if (state is CharacterDetailUiState.Success) state.character.name else
                            stringResource(id = R.string.character_detail_title)
                    )
                },
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is CharacterDetailUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is CharacterDetailUiState.Error -> {
                    Text(text = state.message)
                }

                is CharacterDetailUiState.Success -> {
                    val character = state.character
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AsyncImage(
                            model = character.imageUrl,
                            contentDescription = character.name,
                            modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                        )
                        HorizontalDivider()
                        StatRow("Status", character.status)
                        StatRow("Species", character.species)
                        StatRow("Gender", character.gender)
                        HorizontalDivider()
                        StatRow("Origin", character.origin?.name ?: "Unknown")
                        StatRow("Location", character.location?.name ?: "Unknown")

                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    stat: String,
    value: String,
) {
    Row {
        Text(text = "$stat:", fontWeight = FontWeight.Bold)
        Text(text = value)

    }
}

private class CharacterDetailScreenPreviewParameterProvider :
    CollectionPreviewParameterProvider<CharacterDetailUiState>(
        listOf(
            CharacterDetailUiState.Loading,
            CharacterDetailUiState.Error("Something went wrong"),
            CharacterDetailUiState.Success(character = CharacterSampleData.fakeCharacters.first())
        )
    )

@PreviewLightDark
@Composable
private fun Preview(
    @PreviewParameter(CharacterDetailScreenPreviewParameterProvider::class) state: CharacterDetailUiState
) {
    PreviewContainer {
        CharacterDetailContent(
            state = state,
            onNavigateBackClick = {},
        )
    }
}