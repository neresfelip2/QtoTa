package br.com.qtota.ui.screen.saved_offers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
                Column(
                    Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    ProductList(
                        products = it,
                        navController = navController,
                        savedProducts = it,
                        onHighlightedButtonClick = { viewModel.deleteProduct(it) },
                    )
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