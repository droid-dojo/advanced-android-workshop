package ninja.droiddojo.rickandmorty.analytics

interface AnalyticsTracker {
    fun trackScreen(screenName: String)
    fun trackEvent(name: String, params: Map<String, String> = emptyMap())
}
