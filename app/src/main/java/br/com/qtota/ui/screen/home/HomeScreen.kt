package br.com.qtota.ui.screen.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.HomeProductItem
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.LocationComponent
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.components.SearchTextField
import br.com.qtota.ui.components.StoreListItem
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.defaultPadding

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun HomeScreen(navController: NavHostController, bottomNavController: NavHostController) {

    val viewModel: HomeViewModel = hiltViewModel()

    val homeUIState by viewModel.homeUIState.collectAsState()
    val localityNameState by viewModel.localityNameState.collectAsState()

    when (homeUIState) {
        is UIState.Loading -> LoadingComponent(Modifier.fillMaxSize())
        is UIState.Error -> ErrorComponent(
            stringResource(R.string.error_loading_message),
            Modifier.fillMaxSize()
        )

        is UIState.Success -> {

            val data = (homeUIState as UIState.Success).data
            val listState = rememberLazyListState()
            LazyColumn (state = listState) {

                item {
                    LocationComponent(localityNameState, Modifier.padding(top = defaultPadding, start = defaultPadding))
                }

                item {
                    SearchTextField(
                        Modifier
                            .fillMaxWidth()
                            .padding(defaultPadding),
                    ) { query -> navigateToSearchProduct(bottomNavController, query) }
                }

                item {
                    HomeTitle(stringResource(R.string.cheapests_in_your_area), Modifier.padding(defaultPadding))
                }

                if (data.products.isEmpty()) {
                    item {
                        MessageContent(
                            {
                                Icon(
                                    painterResource(R.drawable.ic_empty_shopping_cart),
                                    null,
                                    Modifier.size(128.dp),
                                    tint = Color(0x59187270)
                                )
                            },
                            stringResource(R.string.any_product_found),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                        )
                    }
                } else {
                    val productRows = data.products.chunked(2)
                    itemsIndexed(productRows) { index, subList ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max)
                                .padding(
                                    start = defaultPadding, end = defaultPadding,
                                    top = if (index == 0) defaultPadding else defaultPadding / 2,
                                    bottom = if (index == productRows.size - 1) defaultPadding else defaultPadding / 2
                                ),
                            horizontalArrangement = Arrangement.spacedBy(defaultPadding),
                        ) {
                            if(subList.size == 2) {
                                HomeProductItem(
                                    product = subList[0],
                                    navController = navController,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                                HomeProductItem(
                                    product = subList[1],
                                    navController = navController,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            } else {
                                HomeProductItem(
                                    product = subList[0],
                                    navController = navController,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                                Spacer(Modifier.weight(1f))
                            }

                        }
                    }

                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            HomeTextButton(stringResource(R.string.see_more_offers)) {
                                navigateToSearchProduct(
                                    bottomNavController
                                )
                            }
                        }
                    }
                }

                /*when (productListState) {
                    is UIState.Loading -> item {
                        LoadingComponent(
                            Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        )
                    }

                    is UIState.Error -> item {
                        ErrorComponent(
                            (productListState as UIState.Error).description,
                            Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        )
                    }

                    is UIState.Success -> {

                    }
                }*/

                item {
                    HomeTitle(
                        stringResource(R.string.nearest_stores),
                        Modifier.padding(defaultPadding)
                    )
                }

                item {
                    if (data.nearbyStores.isEmpty()) {
                        MessageContent(
                            {
                                Icon(
                                    painterResource(R.drawable.outline_store_24), null,
                                    Modifier.size(128.dp),
                                    tint = Color(0x59187270)
                                )
                            }, stringResource(R.string.any_store_found),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        )
                        return@item
                    }

                    LazyRow(contentPadding = PaddingValues(8.dp)) {
                        items(data.nearbyStores) { store ->
                            StoreListItem(store) {
                                bottomNavController.navigate(AppRoute.StoreProducts.route)
                            }
                        }
                    }
                }

                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        HomeTextButton(stringResource(R.string.see_more_stores)) {
                            bottomNavController.navigate(AppRoute.StoreList.route)
                        }
                        /*TextButton(
                            onClick = { *//*…*//* },
                            Modifier
                                .padding(horizontal = defaultPadding)
                                .padding(bottom = defaultPadding),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = DefaultColor,
                                containerColor = Color.Transparent
                            )
                        ) {
                            Text(
                                stringResource(R.string.see_maps),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }*/
                    }
                }

            }

        }
    }

}

private fun navigateToSearchProduct(bottomNavController: NavHostController, query: String? = null) {
    bottomNavController.navigate(AppRoute.SearchProduct.createRoute(query)) {
        popUpTo(bottomNavController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = false
        restoreState = false
    }
}

@Composable
internal fun HomeTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier = modifier,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.DarkGray
    )
}

@Composable
private fun HomeTextButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    TextButton(onClick,
        modifier,
        colors = ButtonDefaults.buttonColors(
            contentColor = DefaultColor,
            containerColor = Color.Transparent
        )
    ) {
        Text(
            text,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(rememberNavController(), rememberNavController())
}

