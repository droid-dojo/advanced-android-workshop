package ninja.droiddojo.rickandmorty.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

@Singleton
class EncryptedSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val cipher: SettingsCipher,
) : SecureSettings {

    override val terminalConfig: Flow<TerminalConfig?> =
        dataStore.data.map { preferences ->
            preferences[CONFIG_KEY]?.let { encoded ->
                val plaintext = cipher.decrypt(Base64.getDecoder().decode(encoded))
                Json.decodeFromString<TerminalConfig>(plaintext.decodeToString())
            }
        }

    override suspend fun save(config: TerminalConfig) {
        val plaintext = Json.encodeToString(TerminalConfig.serializer(), config)
        val encoded = Base64.getEncoder().encodeToString(cipher.encrypt(plaintext.encodeToByteArray()))
        // transactional: the config is written completely or not at all
        dataStore.edit { preferences ->
            preferences[CONFIG_KEY] = encoded
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(CONFIG_KEY)
        }
    }

    companion object {
        private val CONFIG_KEY = stringPreferencesKey("terminal_config")
    }
}
