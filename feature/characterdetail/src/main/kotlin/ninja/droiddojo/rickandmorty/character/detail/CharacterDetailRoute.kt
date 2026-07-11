package ninja.droiddojo.rickandmorty.character.detail

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class CharacterDetailRoute(val id: Int) : NavKey
