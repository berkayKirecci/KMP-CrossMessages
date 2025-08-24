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
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun NativeAlert(message: String?, title: String?, actions: List<Action>?) {
    dispatch_async(dispatch_get_main_queue()) {
        val alertController = createAlertController(message, title, actions)
        setViewContoller(alertController)
    }
}

private fun createAlertController(
    message: String?,
    title: String?, actions:
    List<Action>?
) = UIAlertController().apply {
    this.title = title.orEmpty()
    this.message = message.orEmpty()

    if (actions.isNullOrEmpty()) {
        addAction(
            UIAlertAction.actionWithTitle(
                title = "Ok",
                style = UIAlertActionStyleDefault,
                handler = null
            )
        )
    }
    actions?.forEach { action ->
        addAction(
            UIAlertAction.actionWithTitle(
                title = action.actionTitle,
                style = getStyle(action.style),
                handler = { action.callbak?.invoke() }
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
    val keyWindow: UIWindow? = UIApplication.sharedApplication.windows.firstOrNull {
        (it as? UIWindow)?.isKeyWindow() == true
    } as? UIWindow

    var topViewController = keyWindow?.rootViewController
    while (topViewController?.presentedViewController != null) {
        topViewController = topViewController.presentedViewController
    }

    topViewController?.presentViewController(
        viewControllerToPresent = controller,
        animated = true,
        completion = null
    )
}