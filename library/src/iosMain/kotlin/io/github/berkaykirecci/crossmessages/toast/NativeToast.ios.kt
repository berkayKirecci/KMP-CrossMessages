package io.github.berkaykirecci.crossmessages.toast

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import io.github.berkaykirecci.crossmessages.core.topViewController
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

private const val FADE_DURATION_SECONDS = 0.25
private const val CONTENT_PADDING: CGFloat = 12.0
private const val HORIZONTAL_MARGIN: CGFloat = 20.0
private const val BOTTOM_MARGIN: CGFloat = 50.0

@Composable
internal actual fun NativeToast(
    data: CrossToastData?,
    modifier: Modifier,
    contentWindowInsets: WindowInsets,
) {
    DisposableEffect(data) {
        val container = data?.let { presentToast(it.visuals.message) }
        onDispose { container?.let(::removeToast) }
    }
}

private fun presentToast(message: String): UIView? {
    val presenter = topViewController() ?: return null

    val container = UIView().apply {
        backgroundColor = UIColor.blackColor.colorWithAlphaComponent(0.75)
        layer.cornerRadius = 12.0
        clipsToBounds = true
        translatesAutoresizingMaskIntoConstraints = false
        alpha = 0.0
    }
    val label = UILabel().apply {
        text = message
        textAlignment = NSTextAlignmentCenter
        textColor = UIColor.whiteColor
        font = UIFont.preferredFontForTextStyle("UICTFontTextStyleSubhead")
        adjustsFontForContentSizeCategory = true
        numberOfLines = 0
        translatesAutoresizingMaskIntoConstraints = false
    }

    container.addSubview(label)
    presenter.view.addSubview(container)
    activateConstraints(presenter, container, label)

    UIView.animateWithDuration(
        duration = FADE_DURATION_SECONDS,
        delay = 0.0,
        options = UIViewAnimationOptionCurveEaseOut,
        animations = { container.alpha = 1.0 },
        completion = null,
    )
    return container
}

private fun removeToast(container: UIView) {
    UIView.animateWithDuration(
        duration = FADE_DURATION_SECONDS,
        delay = 0.0,
        options = UIViewAnimationOptionCurveEaseIn,
        animations = { container.alpha = 0.0 },
        completion = { container.removeFromSuperview() },
    )
}

private fun activateConstraints(
    presenter: UIViewController,
    container: UIView,
    label: UIView,
) {
    NSLayoutConstraint.activateConstraints(
        listOf(
            label.topAnchor.constraintEqualToAnchor(container.topAnchor, CONTENT_PADDING),
            label.bottomAnchor.constraintEqualToAnchor(container.bottomAnchor, -CONTENT_PADDING),
            label.leadingAnchor.constraintEqualToAnchor(container.leadingAnchor, CONTENT_PADDING),
            label.trailingAnchor.constraintEqualToAnchor(container.trailingAnchor, -CONTENT_PADDING),

            container.leadingAnchor.constraintGreaterThanOrEqualToAnchor(
                presenter.view.leadingAnchor,
                HORIZONTAL_MARGIN,
            ),
            container.trailingAnchor.constraintLessThanOrEqualToAnchor(
                presenter.view.trailingAnchor,
                -HORIZONTAL_MARGIN,
            ),
            container.centerXAnchor.constraintEqualToAnchor(presenter.view.centerXAnchor),
            container.bottomAnchor.constraintEqualToAnchor(
                presenter.view.safeAreaLayoutGuide.bottomAnchor,
                -BOTTOM_MARGIN,
            ),
        )
    )
}
