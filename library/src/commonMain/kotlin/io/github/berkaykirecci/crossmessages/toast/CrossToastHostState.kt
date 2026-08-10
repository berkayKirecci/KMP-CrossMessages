package io.github.berkaykirecci.crossmessages.toast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

/** The text and duration of a single toast. */
@Immutable
data class CrossToastVisuals(
    val message: String,
    val duration: CrossToastDuration = CrossToastDuration.Short,
)

/** Matches the two lengths every platform toast implementation agrees on. */
enum class CrossToastDuration { Short, Long }

/** Handle to the toast currently on screen. */
@Stable
interface CrossToastData {
    val visuals: CrossToastVisuals

    /** Removes the toast and lets the next queued one through. */
    fun dismiss()
}

/** Holds the toast currently on screen and serializes the ones behind it. */
@Stable
class CrossToastHostState internal constructor(
    private val scope: CoroutineScope,
) {
    private val mutex = Mutex()

    /** The toast being displayed, or `null` when nothing is showing. */
    var currentToastData: CrossToastData? by mutableStateOf(null)
        private set

    /** Shows [visuals], suspending until the toast has been on screen for its full duration. */
    suspend fun showToast(visuals: CrossToastVisuals) {
        mutex.withLock {
            try {
                suspendCancellableCoroutine { continuation ->
                    currentToastData = CrossToastDataImpl(visuals, continuation)
                }
            } finally {
                currentToastData = null
            }
        }
    }

    /** Convenience overload of [showToast]. */
    suspend fun showToast(
        message: String,
        duration: CrossToastDuration = CrossToastDuration.Short,
    ): Unit = showToast(CrossToastVisuals(message, duration))

    /** Fire-and-forget variant for non-coroutine call sites. */
    fun show(
        message: String,
        duration: CrossToastDuration = CrossToastDuration.Short,
    ): Job = scope.launch { showToast(message, duration) }

    /** Dismisses whatever is on screen right now; the next queued toast takes over. */
    fun dismissCurrent() {
        currentToastData?.dismiss()
    }

}

private class CrossToastDataImpl(
    override val visuals: CrossToastVisuals,
    private val continuation: Continuation<Unit>,
) : CrossToastData {
    private var finished = false

    override fun dismiss() {
        if (finished) return
        finished = true
        continuation.resume(Unit)
    }
}

/** Creates a [CrossToastHostState] bound to the current composition's [CoroutineScope]. */
@Composable
fun rememberCrossToastHostState(): CrossToastHostState {
    val scope = rememberCoroutineScope()
    return remember(scope) { CrossToastHostState(scope) }
}
