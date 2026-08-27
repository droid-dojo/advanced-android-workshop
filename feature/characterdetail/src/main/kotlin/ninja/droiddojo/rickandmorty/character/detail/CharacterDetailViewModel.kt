package ninja.droiddojo.rickandmorty.character.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel(assistedFactory = CharacterDetailViewModel.Factory::class)
class CharacterDetailViewModel @AssistedInject constructor(
    private val repository: CharacterRepository,
    @Assisted private val id: Int,
) : ViewModel() {

    private val isRefreshFailed = MutableStateFlow(false)

    val uiState: StateFlow<CharacterDetailUiState> =
        combine(repository.observeCharacter(id), isRefreshFailed) { character, refreshFailed ->
            when {
                character != null -> CharacterDetailUiState.Success(character)
                refreshFailed -> CharacterDetailUiState.Error("Keine Verbindung")
                else -> CharacterDetailUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = CharacterDetailUiState.Loading,
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshFailed.value = false
            try {
                repository.refreshCharacter(id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                isRefreshFailed.value = true
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Int): CharacterDetailViewModel
    }
}
