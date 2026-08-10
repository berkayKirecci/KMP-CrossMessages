package io.github.berkaykirecci.crossmessages.core

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.min

private const val DISTANCE_THRESHOLD_FRACTION = 0.25f
private val MaxDistanceThreshold: Dp = 96.dp
private val VelocityThreshold: Dp = 125.dp
private const val EXIT_DURATION_MILLIS = 200

/** Lets the user flick a transient message off screen horizontally. */
@Composable
internal fun Modifier.swipeToDismiss(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    onDismiss: () -> Unit,
): Modifier {
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    var offsetX by remember { mutableFloatStateOf(0f) }
    var width by remember { mutableFloatStateOf(1f) }

    val density = LocalDensity.current
    val velocityThresholdPx = with(density) { VelocityThreshold.toPx() }
    val maxDistanceThresholdPx = with(density) { MaxDistanceThreshold.toPx() }

    val draggableState = rememberDraggableState { delta -> offsetX += delta }

    val gestures = if (enabled) {
        Modifier.draggable(
            state = draggableState,
            orientation = Orientation.Horizontal,
            interactionSource = interactionSource,
            onDragStopped = { velocity ->
                val travelled = abs(offsetX)
                val distanceThreshold =
                    min(width * DISTANCE_THRESHOLD_FRACTION, maxDistanceThresholdPx)
                val dismissed = travelled >= distanceThreshold ||
                    (abs(velocity) >= velocityThresholdPx && travelled > 0f)

                if (dismissed) {
                    val target = if (offsetX < 0f) -width else width
                    animate(
                        initialValue = offsetX,
                        targetValue = target,
                        initialVelocity = velocity,
                        animationSpec = tween(EXIT_DURATION_MILLIS),
                    ) { value, _ -> offsetX = value }
                    currentOnDismiss()
                } else {
                    animate(
                        initialValue = offsetX,
                        targetValue = 0f,
                        initialVelocity = velocity,
                        animationSpec = spring(),
                    ) { value, _ -> offsetX = value }
                }
            },
        )
    } else {
        Modifier
    }

    return this
        .onSizeChanged { width = it.width.toFloat().coerceAtLeast(1f) }
        .graphicsLayer {
            translationX = offsetX
            alpha = 1f - (abs(offsetX) / width).coerceIn(0f, 1f)
        }
        .then(gestures)
}
