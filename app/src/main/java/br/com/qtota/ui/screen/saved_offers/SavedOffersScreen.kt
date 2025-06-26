package br.com.qtota.ui.screen.saved_offers

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.components.ProductList
import br.com.qtota.ui.components.Toolbar

@Composable
internal fun SavedOffersScreen(navController: NavHostController) {

    val viewModel: SavedOffersViewModel = hiltViewModel()
    val savedProducts by viewModel.savedProducts.collectAsState()

    Scaffold(
        topBar = { Toolbar(backButtonEnabled = navController) }
    ) { innerPadding ->

        savedProducts?.let {
            if(it.isNotEmpty()) {
                LazyColumn(Modifier.padding(innerPadding)) {
                    items(it) { product ->
                        ProductList(product, navController) {
                            viewModel.deleteProduct(product)
                        }
                    }
                }
            } else {
                MessageContent({
                    Icon(
                        painter = painterResource(R.drawable.ic_empty_shopping_cart),
                        contentDescription = null,
                        modifier = Modifier.size(96.dp)
                    )
                }, "Não há nada aqui", Color.Gray)
            }
        }

    }
}

@Composable @Preview(showBackground = true)
private fun SavedOffersScreenPreview() {
    SavedOffersScreen(rememberNavController())
}