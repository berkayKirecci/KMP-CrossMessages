package io.github.berkaykirecci.crossmessages.snackbar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import io.github.berkaykirecci.crossmessages.core.swipeToDismiss
import kotlinx.coroutines.delay

private const val ANIMATION_DURATION_MILLIS = 350
private const val SHORT_DURATION_MILLIS = 4_000L
private const val LONG_DURATION_MILLIS = 10_000L
private const val TIMER_TICK_MILLIS = 50L

/** Renders whatever [hostState] is currently showing. */
@Composable
fun CrossSnackbarHost(
    hostState: CrossSnackbarHostState,
    modifier: Modifier = Modifier,
    contentWindowInsets: WindowInsets = WindowInsets.safeDrawing,
    swipeToDismissEnabled: Boolean = true,
    snackbar: @Composable (CrossSnackbarData) -> Unit = { CrossSnackbar(it) },
) {
    val currentData = hostState.currentSnackbarData
    val accessibilityManager = LocalAccessibilityManager.current

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isDragged by interactionSource.collectIsDraggedAsState()
    val timerPaused = isPressed || isHovered || isDragged

    var position by remember { mutableStateOf(CrossSnackbarPosition.Bottom) }
    LaunchedEffect(currentData) {
        currentData?.let { position = it.visuals.position }
    }

    val elapsed = remember(currentData) { mutableLongStateOf(0L) }
    LaunchedEffect(currentData, timerPaused) {
        val data = currentData ?: return@LaunchedEffect
        if (timerPaused) return@LaunchedEffect
        val total = data.visuals.duration.toMillis(
            hasAction = data.visuals.actionLabel != null,
            hasIcon = CrossSnackbarDefaults.icon(data.visuals) != null,
            accessibilityManager = accessibilityManager,
        )
        if (total == Long.MAX_VALUE) return@LaunchedEffect
        while (elapsed.longValue < total) {
            delay(TIMER_TICK_MILLIS)
            elapsed.longValue += TIMER_TICK_MILLIS
        }
        data.dismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(contentWindowInsets),
        contentAlignment = when (position) {
            CrossSnackbarPosition.Top -> Alignment.TopCenter
            CrossSnackbarPosition.Bottom -> Alignment.BottomCenter
        },
    ) {
        AnimatedContent(
            targetState = currentData,
            transitionSpec = {
                val fromTop = (targetState ?: initialState)?.visuals?.position ==
                    CrossSnackbarPosition.Top
                val offset: (Int) -> Int = { height -> if (fromTop) -height else height }
                (slideInVertically(
                    initialOffsetY = offset,
                    animationSpec = tween(ANIMATION_DURATION_MILLIS, easing = LinearOutSlowInEasing),
                ) + fadeIn(tween(ANIMATION_DURATION_MILLIS, easing = LinearOutSlowInEasing)))
                    .togetherWith(
                        slideOutVertically(
                            targetOffsetY = offset,
                            animationSpec = tween(
                                ANIMATION_DURATION_MILLIS,
                                easing = FastOutLinearInEasing,
                            ),
                        ) + fadeOut(tween(ANIMATION_DURATION_MILLIS, easing = FastOutLinearInEasing))
                    ) using null
            },
            contentAlignment = Alignment.Center,
            label = "CrossSnackbar",
        ) { data ->
            if (data == null) {
                Box(Modifier)
            } else {
                Box(
                    modifier = Modifier
                        .swipeToDismiss(
                            enabled = swipeToDismissEnabled,
                            interactionSource = interactionSource,
                            onDismiss = data::dismiss,
                        )
                        .hoverable(interactionSource)
                        .pointerInput(data) {
                            detectTapGestures(
                                onPress = {
                                    val press = PressInteraction.Press(it)
                                    interactionSource.emit(press)
                                    val released = tryAwaitRelease()
                                    interactionSource.emit(
                                        if (released) PressInteraction.Release(press)
                                        else PressInteraction.Cancel(press)
                                    )
                                }
                            )
                        }
                        .semantics {
                            liveRegion = LiveRegionMode.Polite
                            dismiss {
                                data.dismiss()
                                true
                            }
                        }
                ) {
                    snackbar(data)
                }
            }
        }
    }
}

private fun CrossSnackbarDuration.toMillis(
    hasAction: Boolean,
    hasIcon: Boolean,
    accessibilityManager: AccessibilityManager?,
): Long {
    val original = when (this) {
        CrossSnackbarDuration.Indefinite -> return Long.MAX_VALUE
        CrossSnackbarDuration.Long -> LONG_DURATION_MILLIS
        CrossSnackbarDuration.Short -> SHORT_DURATION_MILLIS
    }
    return accessibilityManager?.calculateRecommendedTimeoutMillis(
        originalTimeoutMillis = original,
        containsIcons = hasIcon,
        containsText = true,
        containsControls = hasAction,
    ) ?: original
}
