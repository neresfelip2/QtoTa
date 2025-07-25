package br.com.qtota.ui.theme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
internal fun ProductTitle(text: String, maxLines: Int = 1, textAlign: TextAlign = TextAlign.Start, modifier: Modifier = Modifier) {
    Text(text,
        modifier = modifier,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 18.sp,
            color = Color.DarkGray,
            textAlign = textAlign
        )
    )
}

@Composable
internal fun ProductDescription(text: String, maxLines: Int = Int.MAX_VALUE) {
    Text(text,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
            color = Color.Gray,
            fontSize = 14.sp,
            lineHeight = 16.sp,
        )
    )
}