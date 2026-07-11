package ninja.droiddojo.rickandmorty.character.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ninja.droiddojo.rickandmorty.character.data.CharacterRepository

@HiltViewModel(assistedFactory = CharacterDetailViewModel.Factory::class)
class CharacterDetailViewModel @AssistedInject constructor(
    private val repository: CharacterRepository,
    @Assisted private val id: Int,
) : ViewModel() {

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

    @AssistedFactory
    interface Factory {
        fun create(id: Int): CharacterDetailViewModel
    }
}
