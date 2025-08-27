package platform

import androidx.compose.runtime.Composable
import state.AlertState

@Composable
internal expect fun NativeAlert(state: AlertState)

data class DialogAction(
    val actionTitle: String,
    val style: ActionStyle = ActionStyle.DEFAULT,
    val callbak: (() -> Unit)? = null
)

enum class ActionStyle {
    DEFAULT,
    CANCEL,
    DESTRUCTIVE
}