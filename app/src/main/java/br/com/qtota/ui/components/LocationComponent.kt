package br.com.qtota.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.qtota.ui.theme.DefaultColor

@Composable
internal fun LocationComponent(localityName: String, modifier: Modifier = Modifier) {
    TextButton(
        {},
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            contentColor = DefaultColor,
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(Icons.Outlined.LocationOn, null, Modifier.size(20.dp), DefaultColor)
        Spacer(Modifier.width(8.dp))
        Text(
            localityName,
            fontWeight = FontWeight.Bold,
            color = DefaultColor,
            fontSize = 14.sp
        )
    }
}