package platform

import android.app.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import platform.ActionStyle.CANCEL
import platform.ActionStyle.DEFAULT
import platform.ActionStyle.DESTRUCTIVE

@Composable
actual fun NativeAlert(
    message: String?,
    title: String?,
    onDismiss: () -> Unit,
    actions: List<DialogAction>?
) {
    val builder = AlertDialog.Builder(LocalContext.current)
        .setTitle(title)
        .setMessage(message)

    if (actions.isNullOrEmpty()) {
        builder.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
            onDismiss()
        }
    }

    actions?.forEach { action ->
        when (action.style) {
            DEFAULT -> builder.setPositiveButton(action.actionTitle) { _, _ ->
                action.callbak?.invoke()
                onDismiss()
            }

            CANCEL -> builder.setNegativeButton(action.actionTitle) { _, _ ->
                action.callbak?.invoke()
                onDismiss()
            }

            DESTRUCTIVE -> builder.setNeutralButton(action.actionTitle) { _, _ ->
                action.callbak?.invoke()
                onDismiss()
            }
        }
    }

    builder.show()
}