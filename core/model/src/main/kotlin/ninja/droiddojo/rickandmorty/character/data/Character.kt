package ninja.droiddojo.rickandmorty.character.data

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val origin: Place?,
    val location: Place?,
    val imageUrl: String,
    val isFavorite: Boolean = false
)

