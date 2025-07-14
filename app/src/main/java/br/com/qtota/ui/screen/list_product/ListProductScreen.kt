package br.com.qtota.ui.screen.list_product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import br.com.qtota.R
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.LocationComponent
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.components.ProductListItem
import br.com.qtota.ui.components.SearchTextField
import br.com.qtota.ui.components.Toolbar
import br.com.qtota.ui.theme.defaultPadding
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

@Composable
internal fun ListProductScreen(navController: NavHostController) {

    val viewModel: ListProductViewModel = hiltViewModel()

    val listProductState by viewModel.listProductState.collectAsState()
    val loadState by viewModel.loadState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Toolbar(
                backButtonEnabled = navController
            )
        },
    ) { innerPadding ->

        if(listProductState.isEmpty()) {
            if(loadState == LoadState.LOADING) {
                LoadingComponent(Modifier
                    .fillMaxSize()
                    .padding(innerPadding))
                return@Scaffold
            }

            if(loadState == LoadState.ERROR) {
                ErrorComponent("Algo deu errado", Modifier
                    .fillMaxSize()
                    .padding(innerPadding))
                return@Scaffold
            }

            if(loadState == LoadState.EMPTY) {
                MessageContent({ Icon(Icons.Outlined.ShoppingCart, null) },
                    stringResource(R.string.any_product_found),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
                return@Scaffold
            }

        } else {

            val listState = rememberLazyListState()

            LaunchedEffect(listState, listProductState.size) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                    .filterNotNull()
                    .distinctUntilChanged()
                    .collect { lastVisible ->
                        if (lastVisible >= listProductState.size && loadState == LoadState.SUCCESS) {
                            viewModel.getProducts()
                        }
                    }
            }

            LazyColumn(
                state = listState, modifier = Modifier.padding(innerPadding)
            ) {
                item { LocationComponent(viewModel.neighborhood, modifier = Modifier.padding(start = defaultPadding, top = defaultPadding)) }
                item { SearchTextField(Modifier
                    .fillMaxWidth()
                    .padding(defaultPadding)) { } }
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

                if(loadState == LoadState.LOADING) {
                    item { Box(Modifier.fillMaxWidth()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) } }
                } else if(loadState == LoadState.EMPTY) {
                    item { Text("Fim da lista", Modifier
                        .fillMaxWidth()
                        .padding(defaultPadding), textAlign = TextAlign.Center) }
                }

            }

        }

    }

}