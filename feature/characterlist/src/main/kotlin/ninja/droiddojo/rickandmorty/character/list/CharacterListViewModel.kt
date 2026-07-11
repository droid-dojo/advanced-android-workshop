package ninja.droiddojo.rickandmorty.character.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ninja.droiddojo.rickandmorty.character.data.CharacterRepository

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val repository: CharacterRepository,
) : ViewModel() {

    private val isRefreshFailed = MutableStateFlow(false)

    val uiState: StateFlow<CharacterListUiState> =
        combine(repository.observeCharacters(), isRefreshFailed) { characters, refreshFailed ->
            when {
                characters.isNotEmpty() -> CharacterListUiState.Success(characters, refreshFailed)
                refreshFailed -> CharacterListUiState.Error("Keine Verbindung")
                else -> CharacterListUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = CharacterListUiState.Loading,
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshFailed.value = false
            try {
                repository.refreshCharacters()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                isRefreshFailed.value = true
            }
        }
    }

    fun toggleFavorite(characterId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(characterId)
        }
    }
}
