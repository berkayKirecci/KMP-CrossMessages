package model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

data class TextModel(
    val textColor: Color = Color.Black,
    val textAlignment: TextAlign = TextAlign.Start
)