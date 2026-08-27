package ninja.droiddojo.rickandmorty.analytics

import android.util.Log
import javax.inject.Inject

class LogcatAnalyticsTracker @Inject constructor() : AnalyticsTracker {

    override fun trackScreen(screenName: String) {
        Log.i(TAG, "screen_view: $screenName")
    }

    override fun trackEvent(name: String, params: Map<String, String>) {
        Log.i(TAG, "event: $name $params")
    }

    companion object {
        private const val TAG = "Analytics"
    }
}
