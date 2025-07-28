package br.com.qtota.ui.screen.saved_offers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import br.com.qtota.ui.components.ImageComponent
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.ErrorColor
import br.com.qtota.ui.theme.ProductTitle
import br.com.qtota.ui.theme.defaultPadding
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShoppingCart
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SavedItemsScreen(navController: NavHostController) {

    val viewModel: SavedItemsViewModel = hiltViewModel()
    val savedProducts by viewModel.savedProducts.collectAsState()

    var selectedSort by rememberSaveable { mutableStateOf(SortType.ALFABETIC) }

    LaunchedEffect(selectedSort) {
        viewModel.loadSavedOffers(selectedSort)
    }

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

            item(span = StaggeredGridItemSpan.FullLine) {

                var expanded by rememberSaveable { mutableStateOf(false) }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(defaultPadding / 2),
                    horizontalAlignment = Alignment.End
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {

                        TextButton({},
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        ) {
                            Icon(
                                painterResource(R.drawable.outline_sort_24),
                                null,
                                tint = DefaultColor
                            )
                            Spacer(Modifier.width(defaultPadding))
                            Text(
                                text = selectedSort.label,
                                fontSize = 14.sp,
                                color = DefaultColor
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            SortType.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        selectedSort = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            items(it) { product ->
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
            Modifier.padding(defaultPadding).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageComponent(savedProduct.product.pathImage, Lucide.ShoppingCart, 128.dp)
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
                pathImage = null,
                createdAt = LocalDateTime.now()
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