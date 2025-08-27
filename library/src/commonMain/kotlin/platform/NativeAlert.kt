package platform

import androidx.compose.runtime.Composable

@Composable
expect fun NativeAlert(
    message: String? = null,
    title: String? = null,
    onDismiss: () -> Unit = {},
    actions: List<DialogAction>? = null
)

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