package io.github.berkaykirecci.crossmessages.toast

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** Chooses between platform-native toast chrome and the shared Compose-rendered one. */
enum class CrossToastStyle {
    /** `android.widget.Toast` on Android and a UIKit overlay on iOS. */
    Native,

    /** The shared Compose surface, identical on every platform. */
    Compose,
}

/** Renders whatever [hostState] is currently showing. */
@Composable
fun CrossToastHost(
    hostState: CrossToastHostState,
    modifier: Modifier = Modifier,
    style: CrossToastStyle = CrossToastStyle.Native,
    contentWindowInsets: WindowInsets = WindowInsets.safeDrawing,
) {
    val data = hostState.currentToastData

    LaunchedEffect(data) {
        val current = data ?: return@LaunchedEffect
        delay(current.visuals.duration.toMillis())
        current.dismiss()
    }

    when (style) {
        CrossToastStyle.Native -> NativeToast(data, modifier, contentWindowInsets)
        CrossToastStyle.Compose -> ComposeToast(data, modifier, contentWindowInsets)
    }
}

/** Presents [data] with platform chrome. */
@Composable
internal expect fun NativeToast(
    data: CrossToastData?,
    modifier: Modifier,
    contentWindowInsets: WindowInsets,
)

/** The shared Compose toast, used directly on desktop and web and available everywhere. */
@Composable
internal fun ComposeToast(
    data: CrossToastData?,
    modifier: Modifier,
    contentWindowInsets: WindowInsets,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(contentWindowInsets),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedContent(
            targetState = data,
            transitionSpec = { (fadeIn(tween(200)) togetherWith fadeOut(tween(300))) using null },
            contentAlignment = Alignment.Center,
            label = "CrossToast",
        ) { current ->
            if (current == null) {
                Box(Modifier)
            } else {
                Surface(
                    modifier = Modifier
                        .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
                        .widthIn(max = 400.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                ) {
                    Text(
                        text = current.visuals.message,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

internal fun CrossToastDuration.toMillis(): Long = when (this) {
    CrossToastDuration.Short -> 2_000L
    CrossToastDuration.Long -> 3_500L
}
