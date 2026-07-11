package ninja.droiddojo.rickandmorty.analytics

import android.util.Log
import javax.inject.Inject

class LogcatLogger @Inject constructor() : AppLogger {

    override fun debug(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }
}
