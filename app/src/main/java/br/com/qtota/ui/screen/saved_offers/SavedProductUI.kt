package br.com.qtota.ui.screen.saved_offers

import br.com.qtota.data.local.entity.Product
import br.com.qtota.ui.state_handler.UIState

data class SavedProductUI(
    val product: Product,
    val offersState: UIState<Int>
)
