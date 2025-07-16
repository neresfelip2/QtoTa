package br.com.qtota.ui.screen.search_product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.LocationComponent
import br.com.qtota.ui.components.ProductListItem
import br.com.qtota.ui.components.SearchTextField
import br.com.qtota.ui.theme.defaultPadding
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

@Composable
internal fun SearchProductScreen(navController: NavHostController, query: String?) {

    val viewModel: SearchProductViewModel = hiltViewModel()

    val listProductState by viewModel.listProductState.collectAsState()
    val loadState by viewModel.loadState.collectAsState()

    val focusManager = LocalFocusManager.current

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
                    query ?: ""
                ) { query ->
                    viewModel.performSearch(query)
                    focusManager.clearFocus()
                }
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

            if (loadState == LoadState.LOADING && listProductState.isNotEmpty()) {
                item { Box(Modifier.fillMaxWidth()) { CircularProgressIndicator(Modifier
                    .padding(defaultPadding)
                    .align(Alignment.Center)) } }
                return@LazyColumn
            } else if (loadState == LoadState.EMPTY) {
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
            if (loadState == LoadState.LOADING) {
                LoadingComponent(
                    Modifier
                        .fillMaxSize()
                        .padding(defaultPadding)
                )
            } else if(loadState == LoadState.ERROR) {
                ErrorComponent(
                    "Algo deu errado",
                    Modifier.fillMaxSize()
                )
            }
        }

    }

    //}

}