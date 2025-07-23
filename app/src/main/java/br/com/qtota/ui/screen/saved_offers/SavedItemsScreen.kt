package br.com.qtota.ui.screen.saved_offers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.data.local.entity.Product
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.ErrorColor
import br.com.qtota.ui.theme.ProductTitle
import br.com.qtota.ui.theme.defaultPadding
import coil.compose.AsyncImage
import java.io.File

@Composable
internal fun SavedItemsScreen(navController: NavHostController) {

    val viewModel: SavedItemsViewModel = hiltViewModel()
    val savedProducts by viewModel.savedProducts.collectAsState()

    savedProducts?.let {
        if (it.isEmpty()) {
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
            return@let
        }

        LazyVerticalStaggeredGrid(
            StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(defaultPadding / 2)
        ) {
            itemsIndexed(it) { index, product ->
                SavedItemCard(
                    product,
                    navController,
                    Modifier.padding(defaultPadding / 2)
                )
            }
        }

    }

}

@Composable
private fun SavedItemCard(savedProduct: SavedProductUI, navController: NavHostController, modifier: Modifier = Modifier) {

    Card(
        onClick = {
            navController.navigate(AppRoute.ProductDetails.productId(savedProduct.product.id))
        },
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            Modifier.padding(defaultPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            savedProduct.product.pathImage?.let {
                AsyncImage(
                    File(it),
                    null,
                    Modifier.size(128.dp)
                )
            } ?: Icon(
                Icons.Outlined.ShoppingCart,
                null,
                Modifier.size(128.dp),
                Color.LightGray
            )
            ProductTitle(savedProduct.product.name, modifier = Modifier.padding(defaultPadding))
            when (savedProduct.offersState) {
                is UIState.Loading -> Row {
                    CircularProgressIndicator(Modifier.size(20.dp), color = DefaultColor)
                    Spacer(Modifier.width(defaultPadding))
                    Text(stringResource(R.string.loading_offers), fontSize = 12.sp, color = DefaultColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                is UIState.Error -> Text(stringResource(R.string.load_offers_error_message), fontSize = 12.sp, lineHeight = 13.sp, color = ErrorColor, textAlign = TextAlign.Center)
                is UIState.Success -> {
                    val numOffers = savedProduct.offersState.data
                    Text(stringResource(R.string.num_offers_found, numOffers), fontSize = 12.sp, lineHeight = 13.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable @Preview(showBackground = true)
private fun SavedItemCardPreview() {
    SavedItemCard(
        SavedProductUI(
            Product(
                id = 0,
                name = "Produto",
                pathImage = null
            ),
            UIState.Loading
        ),
        rememberNavController()
    )
}

@Composable @Preview(showBackground = true)
private fun SavedOffersScreenPreview() {
    SavedItemsScreen(rememberNavController())
}