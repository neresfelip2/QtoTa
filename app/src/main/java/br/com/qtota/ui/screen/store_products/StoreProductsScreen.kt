package br.com.qtota.ui.screen.store_products

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import br.com.qtota.R
import br.com.qtota.data.remote.store.StoreResponse
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.ProductListItem
import br.com.qtota.ui.components.SearchTextField
import br.com.qtota.ui.state_handler.LoadMoreListState
import br.com.qtota.ui.theme.defaultPadding
import coil.compose.AsyncImage

@Composable
internal fun StoreProductsScreen(store: StoreResponse, bottomNavController: NavHostController, navController: NavHostController) {

    val viewModel: StoreProductsViewModel = hiltViewModel()

    val listProductState by viewModel.listProductState.collectAsState()
    val loadState by viewModel.loadState.collectAsState()

    val listState = rememberLazyListState()
    Column {
        LazyColumn(state = listState) {
            item {
                Row {
                    store.logo?.let {
                        AsyncImage(it, null,
                            Modifier.size(64.dp)
                        )
                    } ?: Icon(painterResource(R.drawable.outline_store_24), null, Modifier.size(64.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(store.name)
                }
            }
            item {
                SearchTextField(
                    Modifier
                        .fillMaxWidth()
                        .padding(defaultPadding),
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

            if (loadState == LoadMoreListState.LOADING && listProductState.isNotEmpty()) {
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
            }
        }

    }

}