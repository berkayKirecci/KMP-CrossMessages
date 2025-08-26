package platform

import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow

fun getViewController(): UIViewController? {
    val keyWindow: UIWindow? = UIApplication.sharedApplication.windows.firstOrNull {
        (it as? UIWindow)?.isKeyWindow() == true
    } as? UIWindow

    var topViewController = keyWindow?.rootViewController
    while (topViewController?.presentedViewController != null) {
        topViewController = topViewController.presentedViewController
    }
    return topViewController
}