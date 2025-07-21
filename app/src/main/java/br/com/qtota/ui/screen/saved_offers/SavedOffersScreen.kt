package br.com.qtota.ui.screen.saved_offers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.ui.components.MessageContent

@Composable
internal fun SavedOffersScreen(navController: NavHostController) {

    val viewModel: SavedOffersViewModel = hiltViewModel()
    val savedProducts by viewModel.savedProducts.collectAsState()

    savedProducts?.let {
        if(it.isNotEmpty()) {
            LazyColumn {
                items(it) { product ->
                    /*ProductList(product, navController = navController {
                        viewModel.deleteProduct(product)
                    }, location = )*/
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MessageContent({
                    Icon(
                        painter = painterResource(R.drawable.ic_empty_shopping_cart),
                        contentDescription = null,
                        modifier = Modifier.size(96.dp)
                    )
                }, stringResource(R.string.empty_product_list_message), Color.Gray)
            }
        }
    }

}

@Composable @Preview(showBackground = true)
private fun SavedOffersScreenPreview() {
    SavedOffersScreen(rememberNavController())
}