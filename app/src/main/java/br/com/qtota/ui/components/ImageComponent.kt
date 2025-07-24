package br.com.qtota.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage

@Composable
internal fun ImageComponent(url: String?, @DrawableRes errorImageRes: Int, size: Dp, color: Color? = null, modifier: Modifier = Modifier) {

    url?.let {
        AsyncImage(
            it,
            null,
            modifier.size(size),
            contentScale = ContentScale.Inside,
            colorFilter = color?.let { ColorFilter.tint(color) }
        )
    } ?: Icon(
        painterResource(errorImageRes),
        null,
        modifier.size(size),
         color ?: Color.LightGray
    )

}