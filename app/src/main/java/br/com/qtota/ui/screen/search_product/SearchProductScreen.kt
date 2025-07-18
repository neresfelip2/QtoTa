package br.com.qtota.ui.screen.search_product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import br.com.qtota.R
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.LocationComponent
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.components.ProductListItem
import br.com.qtota.ui.components.SearchTextField
import br.com.qtota.ui.state_handler.LoadMoreListState
import br.com.qtota.ui.theme.defaultPadding
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

@Composable
internal fun SearchProductScreen(navController: NavHostController, query: String?) {

    val viewModel: SearchProductViewModel = hiltViewModel()

    val listProductState by viewModel.listProductState.collectAsState()
    val loadState by viewModel.loadState.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(listState, listProductState.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { lastVisible ->
                if (lastVisible >= listProductState.size && loadState == LoadMoreListState.SUCCESS) {
                    viewModel.getProducts()
                }
            }
    }

    LaunchedEffect(query) {
        viewModel.performSearch(query)
    }

    Column {
        LazyColumn(state = listState) {
            item {
                LocationComponent(
                    viewModel.neighborhood,
                    modifier = Modifier.padding(start = defaultPadding, top = defaultPadding)
                )
            }
            item {
                SearchTextField(
                    Modifier
                        .fillMaxWidth()
                        .padding(defaultPadding),
                    query ?: "",
                    onDone = viewModel::performSearch
                )
            }
            items(listProductState) { product ->
                ProductListItem(
                    product = product,
                    navController = navController,
                    onHighlightedButtonClick = {
                        //viewModel.saveProduct(it)
                    },
                    modifier = Modifier.padding(defaultPadding)
                )
            }

            if(listProductState.isNotEmpty()) {

                if (loadState == LoadMoreListState.LOADING) {
                    item { Box(Modifier.fillMaxWidth()) { CircularProgressIndicator(Modifier
                        .padding(defaultPadding)
                        .align(Alignment.Center)) } }
                    return@LazyColumn
                } else if (loadState == LoadMoreListState.EMPTY) {
                    item {
                        Text(
                            "Fim da lista", Modifier
                                .fillMaxWidth()
                                .padding(defaultPadding), textAlign = TextAlign.Center
                        )
                    }
                }

            }

        }

        if(listProductState.isEmpty()) {
            if (loadState == LoadMoreListState.LOADING) {
                LoadingComponent(
                    Modifier
                        .fillMaxSize()
                        .padding(defaultPadding)
                )
            } else if(loadState == LoadMoreListState.ERROR) {
                ErrorComponent(
                    "Algo deu errado",
                    Modifier.fillMaxSize()
                )
            } else if(loadState == LoadMoreListState.EMPTY) {
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
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

    }

}