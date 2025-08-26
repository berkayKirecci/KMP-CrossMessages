package platform

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import platform.CoreGraphics.CGFloat
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UILabel
import platform.UIKit.UIView
import platform.UIKit.UIViewAnimationOptionCurveEaseIn
import platform.UIKit.UIViewAnimationOptionCurveEaseOut
import platform.UIKit.UIViewController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import state.ToastState

@Composable
actual fun NativeToast(state: ToastState) {
    dispatch_async(dispatch_get_main_queue()) {
        getViewController()?.run {
            val toastContainer = createToastContainer()
            val toastLabel = createToastLabel(state.toastMessage.orEmpty())
            toastContainer.addSubview(toastLabel)
            view.addSubview(toastContainer)

            setConstraints(
                viewController = this,
                toastLabel = toastLabel,
                toastContainer = toastContainer,
                padding = 12.0
            )

            animate(toastContainer)
        }
    }
}

private fun createToastContainer() = UIView().apply {
    backgroundColor = UIColor.blackColor.colorWithAlphaComponent(0.7)
    layer.cornerRadius = 10.0
    clipsToBounds = true
    translatesAutoresizingMaskIntoConstraints = false
    alpha = 0.0
}

private fun createToastLabel(message: String) = UILabel().apply {
    text = message
    textAlignment = NSTextAlignmentCenter
    textColor = UIColor.whiteColor
    font = UIFont.systemFontOfSize(14.0)
    numberOfLines = 0
    translatesAutoresizingMaskIntoConstraints = false
}

private fun setConstraints(
    viewController: UIViewController,
    toastLabel: UIView,
    toastContainer: UIView,
    padding: CGFloat
) {
    NSLayoutConstraint.activateConstraints(
        listOf(
            toastLabel.topAnchor.constraintEqualToAnchor(
                toastContainer.topAnchor,
                constant = padding
            ),
            toastLabel.bottomAnchor.constraintEqualToAnchor(
                toastContainer.bottomAnchor,
                constant = -padding
            ),
            toastLabel.leadingAnchor.constraintEqualToAnchor(
                toastContainer.leadingAnchor,
                constant = padding
            ),
            toastLabel.trailingAnchor.constraintEqualToAnchor(
                toastContainer.trailingAnchor,
                constant = -padding
            )
        )
    )

    NSLayoutConstraint.activateConstraints(
        listOf(
            toastContainer.leadingAnchor.constraintGreaterThanOrEqualToAnchor(
                viewController.view.leadingAnchor,
                constant = 20.0
            ),
            toastContainer.trailingAnchor.constraintLessThanOrEqualToAnchor(
                viewController.view.trailingAnchor,
                constant = -20.0
            ),
            toastContainer.centerXAnchor.constraintEqualToAnchor(viewController.view.centerXAnchor)
        )
    )

    toastContainer.bottomAnchor.constraintEqualToAnchor(
        viewController.view.safeAreaLayoutGuide.bottomAnchor,
        constant = -50.0
    ).active = true
}

private fun animate(toastContainer: UIView) {
    UIView.animateWithDuration(
        0.5,
        delay = 0.0,
        options = UIViewAnimationOptionCurveEaseOut,
        animations = {
            toastContainer.alpha = 1.0
        },
        completion = { _ ->
            UIView.animateWithDuration(
                0.5,
                delay = 2.0,
                options = UIViewAnimationOptionCurveEaseIn,
                animations = {
                    toastContainer.alpha = 0.0
                },
                completion = { _ ->
                    toastContainer.removeFromSuperview()
                }
            )
        }
    )
}