package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import platform.NativeToast
import state.ToastState

private const val DURATION = 2500L
private const val QUEUE_DELAY = 500L

@Composable
fun Toast(state: ToastState) {
    LaunchedEffect(state.toastMessage) {
        if (state.toastMessage != null) {
            delay(DURATION)
            state.clear()
        } else {
            delay(QUEUE_DELAY)
            state.checkQueue()
        }
    }

    if (!state.toastMessage.isNullOrEmpty()) {
        NativeToast(state)
    }
}