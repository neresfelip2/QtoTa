package br.com.qtota.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import br.com.qtota.ui.theme.DefaultColor

@Composable
internal fun LoadingComponent() {
    MessageContent({CircularProgressIndicator(color = DefaultColor)}, "Carregando...")
}