package ninja.droiddojo.rickandmorty.character.list

import ninja.droiddojo.rickandmorty.character.data.Character

sealed interface CharacterListUiState {
    data object Loading : CharacterListUiState
    data class Success(val characters: List<Character>) : CharacterListUiState
    data class Error(val message: String) : CharacterListUiState
}
