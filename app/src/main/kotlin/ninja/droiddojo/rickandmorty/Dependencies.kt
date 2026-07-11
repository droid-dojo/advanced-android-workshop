package ninja.droiddojo.rickandmorty

import kotlinx.serialization.json.Json
import ninja.droiddojo.rickandmorty.character.data.CharacterRepository
import ninja.droiddojo.rickandmorty.character.data.api.RickAndMortyApi
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

object Dependencies {
    private val json = Json { ignoreUnknownKeys = true }
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://rickandmortyapi.com/api/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val rickAndMortyApi: RickAndMortyApi = retrofit.create()

    val characterRepository = CharacterRepository(rickAndMortyApi)
}
