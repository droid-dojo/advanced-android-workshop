package ninja.droiddojo.rickandmorty.character

import ninja.droiddojo.rickandmorty.character.data.Character
import ninja.droiddojo.rickandmorty.character.data.Place

object CharacterSampleData {
    val fakeCharacters = listOf(
        Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            gender = "Male",
            origin = Place(id = 1, name = "Earth (C-137)"),
            location = Place(id = 20, name = "Earth (Replacement Dimension)"),
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
        ),
        Character(
            id = 2,
            name = "Morty Smith",
            status = "Alive",
            species = "Human",
            gender = "Male",
            origin = Place(id = 1, name = "Earth (C-137)"),
            location = Place(id = 20, name = "Earth (Replacement Dimension)"),
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
        ),
        Character(
            id = 3,
            name = "Summer Smith",
            status = "Alive",
            species = "Human",
            gender = "Female",
            origin = Place(id = 20, name = "Earth (Replacement Dimension)"),
            location = Place(id = 20, name = "Earth (Replacement Dimension)"),
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/3.jpeg"
        ),
        Character(
            id = 4,
            name = "Beth Smith",
            status = "Alive",
            species = "Human",
            gender = "Female",
            origin = Place(id = 20, name = "Earth (Replacement Dimension)"),
            location = Place(id = 20, name = "Earth (Replacement Dimension)"),
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/4.jpeg"
        ),
        Character(
            id = 5,
            name = "Jerry Smith",
            status = "Alive",
            species = "Human",
            gender = "Male",
            origin = Place(id = 20, name = "Earth (Replacement Dimension)"),
            location = Place(id = 20, name = "Earth (Replacement Dimension)"),
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/5.jpeg"
        ),
    )
}