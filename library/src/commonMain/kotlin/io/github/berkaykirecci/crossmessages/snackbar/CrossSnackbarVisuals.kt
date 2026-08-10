package io.github.berkaykirecci.crossmessages.snackbar

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.resources.DrawableResource

/** Everything needed to render a single snackbar. */
@Immutable
data class CrossSnackbarVisuals(
    val message: String,
    val actionLabel: String? = null,
    val withDismissAction: Boolean = true,
    val duration: CrossSnackbarDuration = CrossSnackbarDuration.Short,
    val position: CrossSnackbarPosition = CrossSnackbarPosition.Bottom,
    val style: CrossSnackbarStyle = CrossSnackbarStyle.Neutral,
    val icon: CrossSnackbarIcon = CrossSnackbarIcon.FromStyle,
    val colors: CrossSnackbarColors? = null,
    val textAlign: TextAlign? = null,
    val dismissActionContentDescription: String = "Dismiss",
)

/** How long a snackbar stays on screen before it dismisses itself. */
enum class CrossSnackbarDuration {
    /** Roughly four seconds. */
    Short,

    /** Roughly ten seconds. */
    Long,

    /** Stays until [CrossSnackbarData.dismiss] or [CrossSnackbarData.performAction] is called. */
    Indefinite,
}

/** Which edge of the host the snackbar animates in from and rests against. */
enum class CrossSnackbarPosition { Top, Bottom }

/** Semantic palette, resolved against the current [androidx.compose.material3.MaterialTheme]. */
enum class CrossSnackbarStyle { Neutral, Success, Warning, Error, Info }

/** The outcome of a suspending `showSnackbar` call. */
enum class CrossSnackbarResult {
    /** Timed out, swiped away, or dismissed explicitly. */
    Dismissed,

    /** The user tapped the action label. */
    ActionPerformed,
}

/** The leading icon slot. */
@Immutable
sealed interface CrossSnackbarIcon {
    /** Use the icon that matches [CrossSnackbarVisuals.style]; [CrossSnackbarStyle.Neutral] has none. */
    data object FromStyle : CrossSnackbarIcon

    /** Render no leading icon at all. */
    data object None : CrossSnackbarIcon

    /** Render a caller-supplied drawable. */
    data class Resource(val resource: DrawableResource) : CrossSnackbarIcon
}
