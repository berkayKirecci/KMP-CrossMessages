package model

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource

data class SnackbarModel(
    val message: String,
    val backgroundColor: Color,
    val duration: Long = 2000L,
    val leadingIcon: DrawableResource? = null,
    val showActionButton: Boolean = true,
    val actionButtonModel: ActionButtonModel? = null,
    val alignment: Alignment = Alignment.TopCenter
)