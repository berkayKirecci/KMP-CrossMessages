package model

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

data class SnackbarModel(
    val message: String,
    val backgroundColor: Color,
    val duration: Long = 2000L,
    val type: SnackbarType? = null,
    val showActionButton: Boolean = true,
    val actionButtonModel: ActionButtonModel? = ActionButtonModel(),
    val alignment: Alignment = Alignment.TopCenter
)