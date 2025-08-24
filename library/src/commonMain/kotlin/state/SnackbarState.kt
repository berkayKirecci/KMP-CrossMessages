package state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import model.SnackbarModel

class SnackbarState {
    var snackbarModel by mutableStateOf<SnackbarModel?>(null)
        private set

    var temp by mutableStateOf<SnackbarModel?>(null)
        private set

    private val messageQueue = mutableListOf<SnackbarModel>()

    fun show(model: SnackbarModel) {
        if (messageQueue.isEmpty()) {
            snackbarModel = model
        }
        messageQueue.add(model)
    }

    fun clear() {
        temp = snackbarModel
        messageQueue.remove(snackbarModel)
        snackbarModel = null
    }

    fun clearAll() {
        snackbarModel = null
        temp = null
        messageQueue.clear()
    }

    internal fun checkQueue() {
        temp = null
        if (messageQueue.isNotEmpty()) {
            snackbarModel = messageQueue.firstOrNull()
        }
    }
}

@Composable
fun rememberSnackbarState(): SnackbarState = remember { SnackbarState() }