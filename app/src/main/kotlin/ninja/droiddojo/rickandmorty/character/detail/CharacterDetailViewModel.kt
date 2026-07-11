package ninja.droiddojo.rickandmorty.character.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ninja.droiddojo.rickandmorty.Dependencies
import ninja.droiddojo.rickandmorty.character.data.CharacterRepository

class CharacterDetailViewModel(
    private val id: Int,
) : ViewModel() {

    private val repository: CharacterRepository = Dependencies.characterRepository
    val uiState: StateFlow<CharacterDetailUiState>
        field = MutableStateFlow<CharacterDetailUiState>(CharacterDetailUiState.Loading)

    init {
        loadCharacter(id)
    }

    private fun loadCharacter(id: Int) {
        viewModelScope.launch {
            try {
                val character = repository.getCharacter(id)
                uiState.value = CharacterDetailUiState.Success(character)
            } catch (e: Exception) {
                uiState.value = CharacterDetailUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    class Factory(private val id: Int) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CharacterDetailViewModel(id) as T
    }
}
