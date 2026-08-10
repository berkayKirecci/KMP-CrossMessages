package io.github.berkaykirecci.crossmessages.core

import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

/** The view controller a UIKit overlay should attach to. */
internal fun topViewController(): UIViewController? {
    val keyWindow = UIApplication.sharedApplication.connectedScenes
        .filterIsInstance<UIWindowScene>()
        .sortedByDescending { it.activationState == UISceneActivationStateForegroundActive }
        .flatMap { scene -> scene.windows.filterIsInstance<UIWindow>() }
        .let { windows -> windows.firstOrNull { it.isKeyWindow() } ?: windows.firstOrNull() }

    var controller = keyWindow?.rootViewController
    while (controller?.presentedViewController != null) {
        controller = controller.presentedViewController
    }
    return controller
}
