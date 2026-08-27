package ninja.droiddojo.rickandmorty.character.detail

import ninja.droiddojo.rickandmorty.character.data.Character

sealed interface CharacterDetailUiState {
    data object Loading : CharacterDetailUiState
    data class Success(val character: Character) : CharacterDetailUiState
    data class Error(val message: String) : CharacterDetailUiState
}
