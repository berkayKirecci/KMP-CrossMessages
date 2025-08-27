package platform

import androidx.compose.runtime.Composable
import platform.ActionStyle.CANCEL
import platform.ActionStyle.DEFAULT
import platform.ActionStyle.DESTRUCTIVE
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertActionStyleDestructive
import platform.UIKit.UIAlertController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import state.AlertState

@Composable
actual fun NativeAlert(state: AlertState) {
    dispatch_async(dispatch_get_main_queue()) {
        val model = state.alertModel
        val alertController =
            createAlertController(
                message = model?.message,
                title = model?.title,
                actions = model?.actions,
                clear = { state.clear() }
            )
        setViewContoller(alertController)
    }
}

private fun createAlertController(
    message: String?,
    title: String?,
    actions: List<DialogAction>?,
    clear: () -> Unit,
) = UIAlertController().apply {
    this.title = title.orEmpty()
    this.message = message.orEmpty()

    if (actions.isNullOrEmpty()) {
        addAction(
            UIAlertAction.actionWithTitle(
                title = "Ok",
                style = UIAlertActionStyleDefault,
                handler = { clear() }
            )
        )
    }
    actions?.forEach { action ->
        addAction(
            UIAlertAction.actionWithTitle(
                title = action.actionTitle,
                style = getStyle(action.style),
                handler = {
                    action.callbak?.invoke()
                    clear()
                }
            )
        )
    }
}

private fun getStyle(actionStyle: ActionStyle) = when (actionStyle) {
    DEFAULT -> UIAlertActionStyleDefault
    CANCEL -> UIAlertActionStyleCancel
    DESTRUCTIVE -> UIAlertActionStyleDestructive
}

private fun setViewContoller(controller: UIAlertController) {
    getViewController()?.presentViewController(
        viewControllerToPresent = controller,
        animated = true,
        completion = null
    )
}