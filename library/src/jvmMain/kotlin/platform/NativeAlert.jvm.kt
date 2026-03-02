package platform

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import state.AlertState

@Composable
internal actual fun NativeAlert(state: AlertState) {
    val model = state.alertModel
    AlertDialog(
        onDismissRequest = { state.clear() },
        title = {
            Text(model?.title.orEmpty())
        },
        text = {
            Text(model?.message.orEmpty())
        },
        confirmButton = {
            val action = model?.actions?.firstOrNull { it.style == ActionStyle.DEFAULT }
            if (action == null) {
                TextButton({}, "Ok") { state.clear() }
            } else {
                TextButton(action.callbak, action.actionTitle) { state.clear() }
            }
        },
        dismissButton = {
            val action = model?.actions?.firstOrNull { it.style == ActionStyle.CANCEL }
            TextButton(action?.callbak, action?.actionTitle) { state.clear() }
        }
    )
}

@Composable
private fun TextButton(onClick: (() -> Unit)?, text: String?, clear: () -> Unit) {
    TextButton(onClick = {
        onClick?.invoke()
        clear()
    }) {
        Text(text.orEmpty())
    }
}