package br.com.qtota.ui.screen.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.data.remote.home_response.CategoryResponse
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.LocationComponent
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.components.ProductListItem
import br.com.qtota.ui.components.SearchTextField
import br.com.qtota.ui.components.StoreListItem
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.GrayColor
import br.com.qtota.ui.theme.defaultPadding
import coil.compose.AsyncImage

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun HomeScreen(navController: NavHostController, bottomNavController: NavHostController) {

    val viewModel: HomeViewModel = hiltViewModel()

    val homeUIState by viewModel.homeUIState.collectAsState()
    val productListState by viewModel.productListState.collectAsState()
    val localityNameState by viewModel.localityNameState.collectAsState()

    when (homeUIState) {
        is UIState.Loading -> LoadingComponent(Modifier.fillMaxSize())
        is UIState.Error -> ErrorComponent(
            stringResource(R.string.error_loading_message),
            Modifier.fillMaxSize()
        )

        is UIState.Success -> {

            val data = (homeUIState as UIState.Success).data

            var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
            val listState = rememberLazyListState()
            LazyColumn(state = listState) {

                item {
                    LocationComponent(
                        localityNameState,
                        Modifier.padding(start = defaultPadding, top = defaultPadding)
                    )
                }

                item {
                    SearchTextField(
                        Modifier
                            .fillMaxWidth()
                            .padding(defaultPadding),
                    ) { query -> navigateToSearchProduct(bottomNavController, query) }
                }

                item { HomeTitle(stringResource(R.string.cheapests_in_your_area)) }

                item {
                    CategoryTabs(data.categories, selectedIndex) { index, category ->
                        selectedIndex = index
                        viewModel.selectTab(category)
                    }
                }

                when (productListState) {
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
                        val products = (productListState as UIState.Success).data
                        if (products.isEmpty()) {
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
                            items(products) { product ->
                                ProductListItem(
                                    product = product,
                                    navController = navController,
                                    onHighlightedButtonClick = {
                                        viewModel.saveProduct(it)
                                    },
                                    modifier = Modifier.padding(defaultPadding)
                                )
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
                    }
                }

                item { HomeTitle(stringResource(R.string.nearest_stores)) }

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
private fun CategoryTabs(tabs: List<CategoryResponse>, selectedIndex: Int, onClickTab: (Int, CategoryResponse?) -> Unit) {

    ScrollableTabRow(
        edgePadding = defaultPadding,
        selectedTabIndex = selectedIndex,
        indicator = {},
        divider = {},
    ) {

        CategoryTabsItem(stringResource(R.string.all), null, selectedIndex == 0) {
            onClickTab(0, null)
        }

        tabs.forEachIndexed { index, category ->
            CategoryTabsItem(category.name, category.urlIcon, index == (selectedIndex - 1)) {
                onClickTab(index + 1, category)
            }
        }

    }
}

@Composable
private fun CategoryTabsItem(name: String, urlIcon: String?, selected: Boolean, onClick: () -> Unit) {
    Tab(
        modifier = Modifier
            .padding(4.dp)
            .clip(CircleShape)
            .background(if (selected) DefaultColor else GrayColor),
        selectedContentColor = Color.White,
        unselectedContentColor = DefaultColor,
        onClick = onClick,
        selected = selected,
        icon = {
            urlIcon?.let {
                AsyncImage(
                    modifier = Modifier.size(24.dp),
                    model = it,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(if (selected) Color.White else DefaultColor)
                )
            } ?: Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.outline_category_24),
                contentDescription = null,
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = name,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    )
}

@Composable
internal fun HomeTitle(text: String) {
    Text(
        text,
        Modifier.padding(defaultPadding),
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
            fontSize = 14.sp
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(rememberNavController(), rememberNavController())
}

