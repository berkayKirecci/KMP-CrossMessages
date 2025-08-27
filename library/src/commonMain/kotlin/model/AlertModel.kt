package model

import platform.DialogAction

data class AlertModel(
    val message: String,
    val title: String,
    val actions: List<DialogAction> = emptyList()
)