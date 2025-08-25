package model

import androidx.compose.ui.graphics.Color
import com.berkaykirecci.library.generated.resources.Res
import com.berkaykirecci.library.generated.resources.ic_check_circle
import com.berkaykirecci.library.generated.resources.ic_error
import com.berkaykirecci.library.generated.resources.ic_info
import com.berkaykirecci.library.generated.resources.ic_warning

object SnackbarDefaults {

    fun success(message: String) = SnackbarModel(
        message = message,
        backgroundColor = Color(0xff1b5e20),
        leadingIconModel = LeadingIconModel(Res.drawable.ic_check_circle)
    )

    fun warning(message: String) = SnackbarModel(
        message = message,
        backgroundColor = Color(0xffe65100),
        leadingIconModel = LeadingIconModel(Res.drawable.ic_warning)
    )

    fun error(message: String) = SnackbarModel(
        message = message,
        backgroundColor = Color(0xffc62828),
        leadingIconModel = LeadingIconModel(Res.drawable.ic_error)
    )

    fun info(message: String) = SnackbarModel(
        message = message,
        backgroundColor = Color(0xff01579b),
        leadingIconModel = LeadingIconModel(Res.drawable.ic_info)
    )
}