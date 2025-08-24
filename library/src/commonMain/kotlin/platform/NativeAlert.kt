package platform

import androidx.compose.runtime.Composable

@Composable
expect fun NativeAlert(
    message: String? = null,
    title: String? = null,
    actions: List<Action>? = null
)

data class Action(
    val actionTitle: String,
    val style: ActionStyle = ActionStyle.DEFAULT,
    val callbak: (() -> Unit)? = null
)

enum class ActionStyle {
    DEFAULT,
    CANCEL,
    DESTRUCTIVE
}