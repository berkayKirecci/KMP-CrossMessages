package io.github.berkaykirecci.crossmessages.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

/** Holds the snackbar currently on screen and serializes the ones waiting behind it. */
@Stable
class CrossSnackbarHostState internal constructor(
    private val scope: CoroutineScope?,
) {

    private val mutex = Mutex()

    /** The snackbar being displayed, or `null` when nothing is showing. */
    var currentSnackbarData: CrossSnackbarData? by mutableStateOf(null)
        private set

    /** Shows [visuals], suspending until it is dismissed or its action is tapped. */
    suspend fun showSnackbar(visuals: CrossSnackbarVisuals): CrossSnackbarResult =
        mutex.withLock {
            try {
                suspendCancellableCoroutine { continuation ->
                    currentSnackbarData = CrossSnackbarDataImpl(visuals, continuation)
                }
            } finally {
                currentSnackbarData = null
            }
        }

    /** Convenience overload of [showSnackbar] that builds the [CrossSnackbarVisuals] for you. */
    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = true,
        duration: CrossSnackbarDuration =
            if (actionLabel == null) CrossSnackbarDuration.Short else CrossSnackbarDuration.Long,
        position: CrossSnackbarPosition = CrossSnackbarPosition.Bottom,
        style: CrossSnackbarStyle = CrossSnackbarStyle.Neutral,
        icon: CrossSnackbarIcon = CrossSnackbarIcon.FromStyle,
        colors: CrossSnackbarColors? = null,
        textAlign: TextAlign? = null,
    ): CrossSnackbarResult = showSnackbar(
        CrossSnackbarVisuals(
            message = message,
            actionLabel = actionLabel,
            withDismissAction = withDismissAction,
            duration = duration,
            position = position,
            style = style,
            icon = icon,
            colors = colors,
            textAlign = textAlign,
        )
    )

    /** Fire-and-forget variant of [showSnackbar] for non-coroutine call sites. */
    fun show(visuals: CrossSnackbarVisuals): Job =
        requireScope().launch { showSnackbar(visuals) }

    /** Shows [message] with the [CrossSnackbarStyle.Success] palette and a check icon. */
    fun success(
        message: String,
        actionLabel: String? = null,
        duration: CrossSnackbarDuration = CrossSnackbarDuration.Short,
        position: CrossSnackbarPosition = CrossSnackbarPosition.Bottom,
    ): Job = show(message, actionLabel, duration, position, CrossSnackbarStyle.Success)

    /** Shows [message] with the [CrossSnackbarStyle.Warning] palette and a warning icon. */
    fun warning(
        message: String,
        actionLabel: String? = null,
        duration: CrossSnackbarDuration = CrossSnackbarDuration.Short,
        position: CrossSnackbarPosition = CrossSnackbarPosition.Bottom,
    ): Job = show(message, actionLabel, duration, position, CrossSnackbarStyle.Warning)

    /** Shows [message] with the [CrossSnackbarStyle.Error] palette and an error icon. */
    fun error(
        message: String,
        actionLabel: String? = null,
        duration: CrossSnackbarDuration = CrossSnackbarDuration.Long,
        position: CrossSnackbarPosition = CrossSnackbarPosition.Bottom,
    ): Job = show(message, actionLabel, duration, position, CrossSnackbarStyle.Error)

    /** Shows [message] with the [CrossSnackbarStyle.Info] palette and an info icon. */
    fun info(
        message: String,
        actionLabel: String? = null,
        duration: CrossSnackbarDuration = CrossSnackbarDuration.Short,
        position: CrossSnackbarPosition = CrossSnackbarPosition.Bottom,
    ): Job = show(message, actionLabel, duration, position, CrossSnackbarStyle.Info)

    /** Dismisses whatever is on screen right now; the next queued snackbar takes over. */
    fun dismissCurrent() {
        currentSnackbarData?.dismiss()
    }

    private fun show(
        message: String,
        actionLabel: String?,
        duration: CrossSnackbarDuration,
        position: CrossSnackbarPosition,
        style: CrossSnackbarStyle,
    ): Job = show(
        CrossSnackbarVisuals(
            message = message,
            actionLabel = actionLabel,
            duration = duration,
            position = position,
            style = style,
        )
    )

    private fun requireScope(): CoroutineScope = scope ?: throw IllegalStateException(
        "CrossSnackbarHostState was constructed without a CoroutineScope, so the non-suspending " +
            "helpers are unavailable. Build it with rememberCrossSnackbarHostState(), or call the " +
            "suspending showSnackbar() from a coroutine you own."
    )
}

/** Handle to the snackbar currently on screen. */
@Stable
interface CrossSnackbarData {
    val visuals: CrossSnackbarVisuals

    /** Reports [CrossSnackbarResult.ActionPerformed] to the caller and removes the snackbar. */
    fun performAction()

    /** Reports [CrossSnackbarResult.Dismissed] to the caller and removes the snackbar. */
    fun dismiss()
}

private class CrossSnackbarDataImpl(
    override val visuals: CrossSnackbarVisuals,
    private val continuation: Continuation<CrossSnackbarResult>,
) : CrossSnackbarData {
    private var finished = false

    override fun performAction(): Unit = finish(CrossSnackbarResult.ActionPerformed)

    override fun dismiss(): Unit = finish(CrossSnackbarResult.Dismissed)

    private fun finish(result: CrossSnackbarResult) {
        if (finished) return
        finished = true
        continuation.resume(result)
    }
}

/** Creates a state bound to the composition's scope, enabling the non-suspending helpers. */
@Composable
fun rememberCrossSnackbarHostState(): CrossSnackbarHostState {
    val scope = rememberCoroutineScope()
    return remember(scope) { CrossSnackbarHostState(scope) }
}
