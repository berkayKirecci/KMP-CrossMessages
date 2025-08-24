package ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.berkaykirecci.library.generated.resources.Res
import com.berkaykirecci.library.generated.resources.ic_close
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import state.SnackbarState

private const val ANIMATION_DURATION = 400
private const val DEFAULT_DURATION = 2000L
private const val QUEUE_DELAY = 500L

@Composable
fun MultiPlatformSnackbar(
    modifier: Modifier = Modifier,
    state: SnackbarState,
) {
    val model by rememberUpdatedState(state.snackbarModel)
    MultiPlatformSnackbar(
        modifier = modifier,
        state = state,
        isVisible = model != null,
        snackbarMessage = getMessage(state),
        backgroundColor = getColor(state)
    )
}

@Composable
private fun MultiPlatformSnackbar(
    modifier: Modifier = Modifier,
    state: SnackbarState,
    isVisible: Boolean,
    snackbarMessage: String,
    backgroundColor: Color
) {
    LaunchedEffect(state.snackbarModel) {
        if (state.snackbarModel != null) {
            delay(state.snackbarModel?.duration ?: DEFAULT_DURATION)
            state.clear()
        } else {
            delay(QUEUE_DELAY)
            state.checkQueue()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = getAlignment(state)
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> getOffset(state, fullHeight) },
                animationSpec = tween(
                    durationMillis = ANIMATION_DURATION,
                    easing = LinearOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = ANIMATION_DURATION,
                    easing = LinearOutSlowInEasing
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> getOffset(state, fullHeight) },
                animationSpec = tween(
                    durationMillis = ANIMATION_DURATION,
                    easing = FastOutLinearInEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = ANIMATION_DURATION,
                    easing = FastOutLinearInEasing
                )
            )
        ) {
            Row(
                modifier = modifier.fillMaxWidth()
                    .background(backgroundColor)
                    .padding(
                        horizontal = 12.dp,
                        vertical = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                getLeadingIcon(state)?.let { drawable ->
                    Icon(
                        painter = painterResource(drawable),
                        contentDescription = null
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    modifier = Modifier.weight(1f),
                    text = snackbarMessage
                )

                Spacer(Modifier.width(8.dp))

                if (showActionButton(state)) {
                    val interactionSource = remember { MutableInteractionSource() }

                    Icon(
                        modifier = Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            state.snackbarModel?.actionButtonModel?.onActionClick?.run {
                                invoke()
                            } ?: state.clear()
                        },
                        painter = painterResource(getActionButton(state)),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

private fun getMessage(state: SnackbarState): String =
    state.snackbarModel?.message ?: state.temp?.message.orEmpty()

private fun getColor(state: SnackbarState): Color =
    state.snackbarModel?.backgroundColor ?: state.temp?.backgroundColor ?: Color.Transparent

private fun getOffset(state: SnackbarState, fullHeight: Int): Int =
    if ((state.snackbarModel?.alignment ?: state.temp?.alignment) == Alignment.BottomCenter) {
        fullHeight
    } else {
        -fullHeight
    }

private fun getLeadingIcon(state: SnackbarState) =
    state.snackbarModel?.leadingIcon ?: state.temp?.leadingIcon

private fun showActionButton(state: SnackbarState) =
    state.snackbarModel?.showActionButton
        ?: state.temp?.showActionButton
        ?: false


private fun getActionButton(state: SnackbarState) =
    state.snackbarModel?.actionButtonModel?.iconRes
        ?: state.temp?.actionButtonModel?.iconRes
        ?: Res.drawable.ic_close

private fun getAlignment(state: SnackbarState) =
    state.snackbarModel?.alignment
        ?: state.temp?.alignment
        ?: Alignment.BottomCenter
