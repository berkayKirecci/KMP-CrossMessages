package model

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

data class SnackbarModel(
    val message: String,
    val backgroundColor: Color,
    val duration: Long = 2000L,
    val leadingIconModel: LeadingIconModel? = null,
    val textModel: TextModel = TextModel(),
    val showActionButton: Boolean = true,
    val actionButtonModel: ActionButtonModel? = ActionButtonModel(),
    val alignment: Alignment = Alignment.TopCenter
)