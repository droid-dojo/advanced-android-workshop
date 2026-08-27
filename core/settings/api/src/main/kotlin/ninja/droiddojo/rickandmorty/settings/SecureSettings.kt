package ninja.droiddojo.rickandmorty.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
data class TerminalConfig(
    val terminalId: String,
    val apiKey: String,
)

interface SecureSettings {
    val terminalConfig: Flow<TerminalConfig?>
    suspend fun save(config: TerminalConfig)
    suspend fun clear()
}
