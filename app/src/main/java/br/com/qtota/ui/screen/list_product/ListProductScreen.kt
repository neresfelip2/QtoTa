package br.com.qtota.ui.screen.list_product

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import br.com.qtota.ui.components.LocationComponent
import br.com.qtota.ui.components.SearchTextField
import br.com.qtota.ui.components.Toolbar

@Composable
internal fun ListProductScreen(navController: NavHostController) {

    val viewModel: ListProductViewModel = hiltViewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Toolbar(
                backButtonEnabled = navController
            )
        },
    ) { innerPadding ->

        val listState = rememberLazyListState()
        LazyColumn(state = listState, modifier = Modifier.padding(innerPadding)) {
            item { LocationComponent(viewModel.location) }
            item { SearchTextField(Modifier.fillMaxWidth()) { } }

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
                    val products = (productListState as UIState.Success).data
                    if (products.isEmpty()) {
                        item {
                            MessageContent(
                                {
                                    Icon(
                                        Icons.Outlined.ShoppingCart,
                                        null,
                                        Modifier.size(128.dp),
                                        tint = Color(0x59187270)
                                    )
                                },
                                stringResource(R.string.not_product_found),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                            )
                        }
                    } else {
                        items(products) { product ->
                            ProductList(
                                product = product,
                                navController = navController,
                                onHighlightedButtonClick = {
                                    viewModel.saveProduct(it)
                                },
                            )
                        }
                        item {
                            Box(Modifier.fillMaxWidth()) {
                                TextButton(
                                    onClick = { },
                                    Modifier.align(Alignment.Center),
                                    colors = ButtonDefaults.buttonColors(
                                        contentColor = DefaultColor,
                                        containerColor = Color.Transparent
                                    )
                                ) {
                                    Text(
                                        "Ver mais ofertas",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }*/

        }

    }

}