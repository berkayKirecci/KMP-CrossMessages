package state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class ToastState {
    var toastMessage by mutableStateOf<String?>(null)
        private set

    private val messageQueue = mutableListOf<String>()

    fun show(message: String) {
        if (messageQueue.isEmpty()) {
            toastMessage = message
        }
        messageQueue.add(message)
    }

    fun clear() {
        messageQueue.remove(toastMessage)
        toastMessage = null
    }

    internal fun checkQueue() {
        if (messageQueue.isNotEmpty()) {
            toastMessage = messageQueue.firstOrNull()
        }
    }
}

@Composable
fun rememberToastState(): ToastState = remember { ToastState() }