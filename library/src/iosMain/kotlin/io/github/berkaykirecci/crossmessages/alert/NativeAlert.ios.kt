package io.github.berkaykirecci.crossmessages.alert

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import io.github.berkaykirecci.crossmessages.core.topViewController
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertActionStyleDestructive
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert

@Composable
internal actual fun NativeAlert(data: CrossAlertData?, modifier: Modifier) {
    DisposableEffect(data) {
        val controller = data?.let(::presentAlert)
        onDispose {
            if (controller?.presentingViewController != null) {
                controller.dismissViewControllerAnimated(flag = true, completion = null)
            }
        }
    }
}

private fun presentAlert(data: CrossAlertData): UIAlertController? {
    val presenter = topViewController() ?: return null
    val visuals = data.visuals

    val controller = UIAlertController.alertControllerWithTitle(
        title = visuals.title,
        message = visuals.message,
        preferredStyle = UIAlertControllerStyleAlert,
    )

    data.actions.forEachIndexed { index, action ->
        controller.addAction(
            UIAlertAction.actionWithTitle(
                title = action.label,
                style = action.style.toUIAlertActionStyle(),
                handler = { data.performAction(index) },
            )
        )
    }

    presenter.presentViewController(
        viewControllerToPresent = controller,
        animated = true,
        completion = null,
    )
    return controller
}

private fun CrossAlertActionStyle.toUIAlertActionStyle() = when (this) {
    CrossAlertActionStyle.Default -> UIAlertActionStyleDefault
    CrossAlertActionStyle.Cancel -> UIAlertActionStyleCancel
    CrossAlertActionStyle.Destructive -> UIAlertActionStyleDestructive
}
