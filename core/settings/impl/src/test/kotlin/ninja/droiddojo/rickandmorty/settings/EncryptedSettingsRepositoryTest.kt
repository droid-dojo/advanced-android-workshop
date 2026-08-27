package ninja.droiddojo.rickandmorty.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class EncryptedSettingsRepositoryTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val dataStoreScope = CoroutineScope(testDispatcher + Job())

    private lateinit var dataStoreFile: File

    private val config = TerminalConfig(terminalId = "POS-0042", apiKey = "super-secret-key")

    @After
    fun tearDown() {
        dataStoreScope.cancel()
    }

    @Test
    fun `saved config can be read back`() = testScope.runTest {
        val repository = createRepository()

        repository.save(config)

        assertEquals(config, repository.terminalConfig.first())
    }

    @Test
    fun `clear removes the config`() = testScope.runTest {
        val repository = createRepository()
        repository.save(config)

        repository.clear()

        assertNull(repository.terminalConfig.first())
    }

    @Test
    fun `nothing is stored in plaintext`() = testScope.runTest {
        val repository = createRepository()

        repository.save(config)
        assertEquals(config, repository.terminalConfig.first()) // force the write to settle

        val rawBytes = dataStoreFile.readBytes().decodeToString()
        assertTrue(dataStoreFile.exists())
        assertFalse("terminal id leaked as plaintext", rawBytes.contains("POS-0042"))
        assertFalse("api key leaked as plaintext", rawBytes.contains("super-secret-key"))
    }

    private fun createRepository(): EncryptedSettingsRepository {
        dataStoreFile = File(temporaryFolder.newFolder(), "secure_settings.preferences_pb")
        val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
            scope = dataStoreScope,
            produceFile = { dataStoreFile },
        )
        return EncryptedSettingsRepository(dataStore, FakeSettingsCipher())
    }
}
