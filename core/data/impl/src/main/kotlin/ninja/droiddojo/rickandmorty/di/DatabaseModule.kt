package ninja.droiddojo.rickandmorty.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ninja.droiddojo.rickandmorty.character.data.db.CharacterDao
import ninja.droiddojo.rickandmorty.character.data.db.RickAndMortyDatabase

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RickAndMortyDatabase =
        Room.databaseBuilder(context, RickAndMortyDatabase::class.java, "rickandmorty.db")
            .build()

    @Provides
    @Singleton
    fun provideCharacterDao(database: RickAndMortyDatabase): CharacterDao =
        database.characterDao()
}
