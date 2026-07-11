package ninja.droiddojo.rickandmorty.character.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import ninja.droiddojo.rickandmorty.PreviewContainer
import ninja.droiddojo.rickandmorty.character.data.Character
import ninja.droiddojo.rickandmorty.character.CharacterSampleData

@Composable
fun CharacterItem(
    character: Character,
    modifier: Modifier = Modifier,
    onFavoriteClick: () -> Unit,
    onItemClick: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onItemClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = "${character.name}'s avatar",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Status: ${character.status}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Icon(
                imageVector = if (character.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (character.isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (character.isFavorite) Color.Red else Color.LightGray,
                modifier = Modifier.clickable(onClick = onFavoriteClick)
            )
        }
    }
}

private class CharacterItemPreviewParameterProvider : CollectionPreviewParameterProvider<Character>(
    listOf(
        CharacterSampleData.fakeCharacters.first(),
        CharacterSampleData.fakeCharacters.first().copy(isFavorite = true),
    )
)

@OptIn(ExperimentalCoilApi::class)
@PreviewLightDark
@PreviewDynamicColors
@Composable
private fun CharacterItemPreview(
    @PreviewParameter(CharacterItemPreviewParameterProvider::class) character: Character
) {
    PreviewContainer {
        CharacterItem(
            character = character,
            onFavoriteClick = {},
            onItemClick = {},
        )
    }
}
