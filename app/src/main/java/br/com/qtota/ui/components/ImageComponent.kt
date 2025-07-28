package br.com.qtota.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage

@Composable
internal fun ImageComponent(url: String?, errorImage: ImageVector, size: Dp, color: Color? = null, modifier: Modifier = Modifier) {

    url?.let {
        AsyncImage(
            it,
            null,
            modifier.size(size),
            colorFilter = color?.let { ColorFilter.tint(color) },
        )
    } ?: Icon(
        errorImage,
        null,
        modifier.size(size),
         color ?: Color.LightGray
    )

}