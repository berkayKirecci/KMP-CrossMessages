package model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import io.github.berkaykirecci.library.generated.resources.Res
import io.github.berkaykirecci.library.generated.resources.ic_check_circle
import io.github.berkaykirecci.library.generated.resources.ic_error
import io.github.berkaykirecci.library.generated.resources.ic_info
import io.github.berkaykirecci.library.generated.resources.ic_warning

data class DefaultSnackbarColors(
    val successColor: Color = Color(0xff1b5e20),
    val warningColor: Color = Color(0xffe65100),
    val errorColor: Color = Color(0xffc62828),
    val infoColor: Color = Color(0xff01579b),
    val textColor: Color = Color.White,
    val leadingIconColor: Color = Color.White,
    val actionButtonColor: Color = Color.White
)

val LocalSnackbarColors = staticCompositionLocalOf { DefaultSnackbarColors() }

class SnackbarDefaults internal constructor(private val colors: DefaultSnackbarColors) {

    fun success(message: String) = SnackbarModel(
        message = message,
        backgroundColor = colors.successColor,
        textModel = TextModel(colors.textColor),
        leadingIconModel = LeadingIconModel(
            iconRes = Res.drawable.ic_check_circle,
            iconTint = colors.leadingIconColor
        ),
        actionButtonModel = ActionButtonModel(iconTint = colors.actionButtonColor)
    )

    fun warning(message: String) = SnackbarModel(
        message = message,
        backgroundColor = colors.warningColor,
        textModel = TextModel(colors.textColor),
        leadingIconModel = LeadingIconModel(
            iconRes = Res.drawable.ic_warning,
            iconTint = colors.leadingIconColor
        ),
        actionButtonModel = ActionButtonModel(iconTint = colors.actionButtonColor)
    )

    fun error(message: String) = SnackbarModel(
        message = message,
        backgroundColor = colors.errorColor,
        textModel = TextModel(colors.textColor),
        leadingIconModel = LeadingIconModel(
            iconRes = Res.drawable.ic_error,
            iconTint = colors.leadingIconColor
        ),
        actionButtonModel = ActionButtonModel(iconTint = colors.actionButtonColor)
    )

    fun info(message: String) = SnackbarModel(
        message = message,
        backgroundColor = colors.infoColor,
        textModel = TextModel(colors.textColor),
        leadingIconModel = LeadingIconModel(
            iconRes = Res.drawable.ic_info,
            iconTint = colors.leadingIconColor
        ),
        actionButtonModel = ActionButtonModel(iconTint = colors.actionButtonColor)
    )
}

@Composable
fun rememberSnackbarDefaults(): SnackbarDefaults {
    val colors = LocalSnackbarColors.current
    return remember(colors) {
        SnackbarDefaults(colors)
    }
}