package ninja.droiddojo.rickandmorty.character.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import ninja.droiddojo.rickandmorty.PreviewContainer
import ninja.droiddojo.rickandmorty.feature.characterlist.test.R

/**
 * Forces inspection mode so Coil never touches the network, and swaps the
 * color placeholder from PreviewContainer for a real drawable from the test
 * resources - still deterministic, but the screenshots also cover actual
 * bitmap decoding and scaling.
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
fun ScreenshotContainer(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val previewHandler = AsyncImagePreviewHandler {
        context.getDrawable(R.drawable.preview_character)!!.asImage()
    }
    CompositionLocalProvider(LocalInspectionMode provides true) {
        PreviewContainer(darkTheme = darkTheme) {
            CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
                content()
            }
        }
    }
}
