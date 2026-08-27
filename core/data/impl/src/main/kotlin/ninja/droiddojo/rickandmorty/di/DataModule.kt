package ninja.droiddojo.rickandmorty.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ninja.droiddojo.rickandmorty.character.data.CharacterRepository
import ninja.droiddojo.rickandmorty.character.data.OfflineFirstCharacterRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(impl: OfflineFirstCharacterRepository): CharacterRepository
}
