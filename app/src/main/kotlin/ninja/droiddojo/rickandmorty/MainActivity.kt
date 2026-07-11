package ninja.droiddojo.rickandmorty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import ninja.droiddojo.rickandmorty.character.detail.CharacterDetailRoute
import ninja.droiddojo.rickandmorty.character.detail.CharacterDetailScreen
import ninja.droiddojo.rickandmorty.character.detail.CharacterDetailViewModel
import ninja.droiddojo.rickandmorty.character.list.CharacterListRoute
import ninja.droiddojo.rickandmorty.character.list.CharacterListScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RickAndMortyTheme {
                val backStack = rememberNavBackStack(CharacterListRoute)

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                    transitionSpec = {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    },
                    popTransitionSpec = {
                        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                    },
                    entryProvider = entryProvider {
                        entry<CharacterListRoute> {
                            CharacterListScreen(
                                onCharacterClick = { id ->
                                    backStack.add(CharacterDetailRoute(id))
                                }
                            )
                        }
                        entry<CharacterDetailRoute> { key ->
                            CharacterDetailScreen(
                                viewModel = hiltViewModel(
                                    key = key.toString(),
                                    creationCallback = { factory: CharacterDetailViewModel.Factory ->
                                        factory.create(
                                            key.id
                                        )
                                    }
                                ),
                                onNavigateBack = { backStack.removeLastOrNull() },
                            )
                        }
                    }
                )
            }
        }
    }
}
