package ninja.droiddojo.rickandmorty.settings.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ninja.droiddojo.rickandmorty.settings.EncryptedSettingsRepository
import ninja.droiddojo.rickandmorty.settings.KeystoreSettingsCipher
import ninja.droiddojo.rickandmorty.settings.SecureSettings
import ninja.droiddojo.rickandmorty.settings.SettingsCipher

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bindSettingsCipher(impl: KeystoreSettingsCipher): SettingsCipher

    @Binds
    @Singleton
    abstract fun bindSecureSettings(impl: EncryptedSettingsRepository): SecureSettings

    companion object {
        @Provides
        @Singleton
        fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            PreferenceDataStoreFactory.create {
                context.preferencesDataStoreFile("secure_settings")
            }
    }
}
