package ninja.droiddojo.rickandmorty.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf

// No-op default keeps previews and tests quiet if no real tracker is provided
val LocalAnalyticsTracker = staticCompositionLocalOf<AnalyticsTracker> { NoOpAnalyticsTracker }

private object NoOpAnalyticsTracker : AnalyticsTracker {
    override fun trackScreen(screenName: String) = Unit
    override fun trackEvent(name: String, params: Map<String, String>) = Unit
}

@Composable
fun TrackScreen(screenName: String) {
    val tracker = LocalAnalyticsTracker.current
    // Exactly one impression per screen visit, not one per recomposition
    LaunchedEffect(screenName) {
        tracker.trackScreen(screenName)
    }
}
