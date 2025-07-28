package br.com.qtota.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.qtota.ui.theme.ErrorColor
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TriangleAlert

@Composable
internal fun ErrorComponent(message: String, modifier: Modifier = Modifier) {
    MessageContent({
        Icon(
            imageVector = Lucide.TriangleAlert,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = ErrorColor
        )
    }, message, ErrorColor, modifier)
}