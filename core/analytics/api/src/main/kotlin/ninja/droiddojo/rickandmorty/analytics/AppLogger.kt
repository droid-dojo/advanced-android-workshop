package ninja.droiddojo.rickandmorty.analytics

interface AppLogger {
    fun debug(tag: String, message: String)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}
