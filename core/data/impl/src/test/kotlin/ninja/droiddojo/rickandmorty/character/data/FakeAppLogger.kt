package ninja.droiddojo.rickandmorty.character.data

import ninja.droiddojo.rickandmorty.analytics.AppLogger

class FakeAppLogger : AppLogger {

    data class LogEntry(val tag: String, val message: String, val throwable: Throwable? = null)

    val debugEntries = mutableListOf<LogEntry>()
    val errorEntries = mutableListOf<LogEntry>()

    override fun debug(tag: String, message: String) {
        debugEntries += LogEntry(tag, message)
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        errorEntries += LogEntry(tag, message, throwable)
    }
}
