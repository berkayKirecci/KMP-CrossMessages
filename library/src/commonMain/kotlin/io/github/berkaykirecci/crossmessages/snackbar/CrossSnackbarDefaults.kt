package io.github.berkaykirecci.crossmessages.snackbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.berkaykirecci.crossmessages.resources.Res
import io.github.berkaykirecci.crossmessages.resources.ic_check_circle
import io.github.berkaykirecci.crossmessages.resources.ic_error
import io.github.berkaykirecci.crossmessages.resources.ic_info
import io.github.berkaykirecci.crossmessages.resources.ic_warning
import org.jetbrains.compose.resources.DrawableResource

/** Container, content, icon and action colors for one snackbar. */
@Immutable
data class CrossSnackbarColors(
    val containerColor: Color,
    val contentColor: Color,
    val iconColor: Color = contentColor,
    val actionColor: Color = contentColor,
)

/** Per-style palette overrides supplied through [LocalCrossSnackbarColors]. */
@Immutable
data class CrossSnackbarStyleColors(
    val neutral: CrossSnackbarColors? = null,
    val success: CrossSnackbarColors? = null,
    val warning: CrossSnackbarColors? = null,
    val error: CrossSnackbarColors? = null,
    val info: CrossSnackbarColors? = null,
)

/** Overrides the semantic snackbar palettes for a subtree; `null` entries keep the theme default. */
val LocalCrossSnackbarColors: androidx.compose.runtime.ProvidableCompositionLocal<CrossSnackbarStyleColors?> =
    compositionLocalOf { null }

/** Shared defaults for [CrossSnackbarHost] and [CrossSnackbar]. */
object CrossSnackbarDefaults {
    /** Material 3 snackbar resting elevation. */
    val Elevation: Dp = 6.dp

    /** Material 3 caps snackbar width so the line length stays readable on tablets and desktop. */
    val MaxWidth: Dp = 600.dp

    /** Inset between the snackbar and the edges of its host. */
    val Margin: Dp = 12.dp

    internal val ContentPadding: PaddingValues =
        PaddingValues(start = 16.dp, top = 6.dp, end = 8.dp, bottom = 6.dp)

    internal val IconSize: Dp = 24.dp

    /** Material 3 uses the extra-small shape for snackbars. */
    val shape: Shape
        @Composable @ReadOnlyComposable get() = MaterialTheme.shapes.extraSmall

    /** The Material 3 default snackbar palette: an inverse-surface container. */
    val neutralColors: CrossSnackbarColors
        @Composable @ReadOnlyComposable get() = CrossSnackbarColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            iconColor = MaterialTheme.colorScheme.inverseOnSurface,
            actionColor = MaterialTheme.colorScheme.inversePrimary,
        )

    /** Error styling taken straight from the theme, so it tracks any custom color scheme. */
    val errorColors: CrossSnackbarColors
        @Composable @ReadOnlyComposable get() = CrossSnackbarColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        )

    val successColors: CrossSnackbarColors
        @Composable @ReadOnlyComposable get() = semanticColors(
            lightContainer = Color(0xFFBEF2C4),
            lightContent = Color(0xFF002110),
            darkContainer = Color(0xFF00522B),
            darkContent = Color(0xFFA8F5BE),
        )

    val warningColors: CrossSnackbarColors
        @Composable @ReadOnlyComposable get() = semanticColors(
            lightContainer = Color(0xFFFFDEA6),
            lightContent = Color(0xFF271900),
            darkContainer = Color(0xFF5D4200),
            darkContent = Color(0xFFFFDEA6),
        )

    val infoColors: CrossSnackbarColors
        @Composable @ReadOnlyComposable get() = semanticColors(
            lightContainer = Color(0xFFD3E3FF),
            lightContent = Color(0xFF001B3D),
            darkContainer = Color(0xFF00458E),
            darkContent = Color(0xFFD6E3FF),
        )

    /** Resolves the palette for [style], honouring any [LocalCrossSnackbarColors] override. */
    @Composable
    @ReadOnlyComposable
    fun colors(style: CrossSnackbarStyle): CrossSnackbarColors {
        val overrides = LocalCrossSnackbarColors.current
        return when (style) {
            CrossSnackbarStyle.Neutral -> overrides?.neutral ?: neutralColors
            CrossSnackbarStyle.Success -> overrides?.success ?: successColors
            CrossSnackbarStyle.Warning -> overrides?.warning ?: warningColors
            CrossSnackbarStyle.Error -> overrides?.error ?: errorColors
            CrossSnackbarStyle.Info -> overrides?.info ?: infoColors
        }
    }

    /** Resolves the palette for [visuals], preferring an explicit [CrossSnackbarVisuals.colors]. */
    @Composable
    @ReadOnlyComposable
    fun colors(visuals: CrossSnackbarVisuals): CrossSnackbarColors =
        visuals.colors ?: colors(visuals.style)

    /** The built-in leading icon for [style], or `null` for [CrossSnackbarStyle.Neutral]. */
    fun icon(style: CrossSnackbarStyle): DrawableResource? = when (style) {
        CrossSnackbarStyle.Neutral -> null
        CrossSnackbarStyle.Success -> Res.drawable.ic_check_circle
        CrossSnackbarStyle.Warning -> Res.drawable.ic_warning
        CrossSnackbarStyle.Error -> Res.drawable.ic_error
        CrossSnackbarStyle.Info -> Res.drawable.ic_info
    }

    /** Resolves the leading icon for [visuals] against its [CrossSnackbarIcon] slot. */
    fun icon(visuals: CrossSnackbarVisuals): DrawableResource? = when (val slot = visuals.icon) {
        CrossSnackbarIcon.None -> null
        CrossSnackbarIcon.FromStyle -> icon(visuals.style)
        is CrossSnackbarIcon.Resource -> slot.resource
    }

    @Composable
    @ReadOnlyComposable
    private fun semanticColors(
        lightContainer: Color,
        lightContent: Color,
        darkContainer: Color,
        darkContent: Color,
    ): CrossSnackbarColors {
        val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
        return CrossSnackbarColors(
            containerColor = if (isDark) darkContainer else lightContainer,
            contentColor = if (isDark) darkContent else lightContent,
        )
    }
}
