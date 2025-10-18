package state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import model.SnackbarDefaults
import model.SnackbarModel
import model.rememberSnackbarDefaults

class SnackbarState internal constructor(private val snackbarDefaults: SnackbarDefaults) {
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

    fun success(message: String) {
        show(snackbarDefaults.success(message))
    }

    fun warning(message: String) {
        show(snackbarDefaults.warning(message))
    }

    fun error(message: String) {
        show(snackbarDefaults.error(message))
    }

    fun info(message: String) {
        show(snackbarDefaults.info(message))
    }

    fun clear() {
        temp = snackbarModel
        messageQueue.remove(snackbarModel)
        snackbarModel = null
    }

    internal fun checkQueue() {
        temp = null
        if (messageQueue.isNotEmpty()) {
            snackbarModel = messageQueue.firstOrNull()
        }
    }
}

@Composable
fun rememberSnackbarState(): SnackbarState {
    val snackbarDefaults = rememberSnackbarDefaults()
    return remember(snackbarDefaults) {
        SnackbarState(snackbarDefaults)
    }
}
