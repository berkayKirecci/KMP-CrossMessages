package state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import model.AlertModel

class AlertState {
    var alertModel by mutableStateOf<AlertModel?>(null)
        private set


    fun show(model: AlertModel) {
        alertModel = model
    }

    fun clear() {
        alertModel = null
    }

}

@Composable
fun rememberAlertState(): AlertState = remember { AlertState() }