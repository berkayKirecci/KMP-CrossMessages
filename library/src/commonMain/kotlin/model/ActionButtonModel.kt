package model

import org.jetbrains.compose.resources.DrawableResource

data class ActionButtonModel(
    val iconRes: DrawableResource? = null,
    val onActionClick: (() -> Unit)? = null
)