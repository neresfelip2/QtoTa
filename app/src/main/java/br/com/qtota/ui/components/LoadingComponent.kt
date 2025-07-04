package br.com.qtota.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.qtota.ui.theme.DefaultColor

@Composable
internal fun LoadingComponent(modifier: Modifier = Modifier) {
    MessageContent({CircularProgressIndicator(color = DefaultColor)}, "Carregando...", modifier = modifier)
}