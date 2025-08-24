package model

import androidx.compose.ui.graphics.Color

object SnackbarDefaults {

    fun success(message: String) = SnackbarModel(
        message = message,
        type = SnackbarType.SUCCESS,
        backgroundColor = Color(0xff1b5e20)
    )

    fun warning(message: String) = SnackbarModel(
        message = message,
        type = SnackbarType.WARNING,
        backgroundColor = Color(0xffe65100)
    )

    fun error(message: String) = SnackbarModel(
        message = message,
        type = SnackbarType.ERROR,
        backgroundColor = Color(0xffc62828)
    )

    fun info(message: String) = SnackbarModel(
        message = message,
        type = SnackbarType.INFO,
        backgroundColor = Color(0xff01579b)
    )
}