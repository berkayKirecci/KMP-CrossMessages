package io.github.berkaykirecci.crossmessages.alert

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

/** Title, body and buttons of a modal alert. */
data class CrossAlertVisuals(
    val title: String,
    val message: String? = null,
    val actions: ImmutableList<CrossAlertAction> = persistentListOf(),
    val dismissible: Boolean = true,
    val defaultConfirmLabel: String = "OK",
)

/** One button in a [CrossAlertVisuals]. */
@Immutable
data class CrossAlertAction(
    val label: String,
    val style: CrossAlertActionStyle = CrossAlertActionStyle.Default,
    val onClick: (() -> Unit)? = null,
)

/** Maps onto `UIAlertActionStyle` on iOS and to Material 3 button emphasis elsewhere. */
enum class CrossAlertActionStyle { Default, Cancel, Destructive }

/** The outcome of a suspending [CrossAlertHostState.showAlert] call. */
sealed interface CrossAlertResult {
    /** The alert was dismissed without choosing an action. */
    data object Dismissed : CrossAlertResult

    /** [action] at [index] in [CrossAlertVisuals.actions] was tapped. */
    data class ActionPerformed(
        val index: Int,
        val action: CrossAlertAction,
    ) : CrossAlertResult
}

/** Handle to the alert currently on screen. */
@Stable
interface CrossAlertData {
    val visuals: CrossAlertVisuals

    /** The buttons actually rendered — [CrossAlertVisuals.actions], or a synthesised confirm button. */
    val actions: ImmutableList<CrossAlertAction>

    /** Invokes the action's callback, reports it to the caller, and closes the alert. */
    fun performAction(index: Int)

    /** Reports [CrossAlertResult.Dismissed] to the caller and closes the alert. */
    fun dismiss()
}

/** Holds the alert currently on screen and serializes the ones behind it. */
@Stable
class CrossAlertHostState internal constructor(
    private val scope: CoroutineScope?,
) {
    constructor() : this(scope = null)

    private val mutex = Mutex()

    /** The alert being displayed, or `null` when nothing is showing. */
    var currentAlertData: CrossAlertData? by mutableStateOf(null)
        private set

    /** Shows [visuals], suspending until the user picks an action or dismisses the alert. */
    suspend fun showAlert(visuals: CrossAlertVisuals): CrossAlertResult =
        mutex.withLock {
            try {
                suspendCancellableCoroutine { continuation ->
                    currentAlertData = CrossAlertDataImpl(visuals, continuation)
                }
            } finally {
                currentAlertData = null
            }
        }

    /** Convenience overload of [showAlert]. */
    suspend fun showAlert(
        title: String,
        message: String? = null,
        actions: List<CrossAlertAction> = emptyList(),
        dismissible: Boolean = true,
    ): CrossAlertResult =
        showAlert(CrossAlertVisuals(title, message, actions.toImmutableList(), dismissible))

    /** Fire-and-forget variant for non-coroutine call sites. */
    fun show(visuals: CrossAlertVisuals): Job =
        requireScope().launch { showAlert(visuals) }

    /** Convenience overload of [show]. */
    fun show(
        title: String,
        message: String? = null,
        actions: List<CrossAlertAction> = emptyList(),
        dismissible: Boolean = true,
    ): Job = show(CrossAlertVisuals(title, message, actions.toImmutableList(), dismissible))

    /** Closes whatever alert is on screen right now. */
    fun dismissCurrent() {
        currentAlertData?.dismiss()
    }

    private fun requireScope(): CoroutineScope = scope ?: error(
        "CrossAlertHostState was constructed without a CoroutineScope, so show() is unavailable. " +
            "Build it with rememberCrossAlertHostState(), or call the suspending showAlert() from " +
            "a coroutine you own."
    )
}

private class CrossAlertDataImpl(
    override val visuals: CrossAlertVisuals,
    private val continuation: Continuation<CrossAlertResult>,
) : CrossAlertData {
    override val actions: ImmutableList<CrossAlertAction> = visuals.actions.ifEmpty {
        persistentListOf(CrossAlertAction(visuals.defaultConfirmLabel))
    }

    private var finished = false

    override fun performAction(index: Int) {
        val action = actions.getOrNull(index) ?: return dismiss()
        action.onClick?.invoke()
        finish(CrossAlertResult.ActionPerformed(index, action))
    }

    override fun dismiss(): Unit = finish(CrossAlertResult.Dismissed)

    private fun finish(result: CrossAlertResult) {
        if (finished) return
        finished = true
        continuation.resume(result)
    }
}

/** Creates a [CrossAlertHostState] bound to the current composition's [CoroutineScope]. */
@Composable
fun rememberCrossAlertHostState(): CrossAlertHostState {
    val scope = rememberCoroutineScope()
    return remember(scope) { CrossAlertHostState(scope) }
}
