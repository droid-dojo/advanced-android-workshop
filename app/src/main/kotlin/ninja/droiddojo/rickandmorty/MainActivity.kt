package ninja.droiddojo.rickandmorty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import ninja.droiddojo.rickandmorty.character.detail.CharacterDetailRoute
import ninja.droiddojo.rickandmorty.character.detail.CharacterDetailScreen
import ninja.droiddojo.rickandmorty.character.detail.CharacterDetailViewModel
import ninja.droiddojo.rickandmorty.character.list.CharacterListRoute
import ninja.droiddojo.rickandmorty.character.list.CharacterListScreen

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
                                viewModel = viewModel(
                                    factory = CharacterDetailViewModel.Factory(key.id)
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
