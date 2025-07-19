package br.com.qtota.ui.screen.search_product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import br.com.qtota.R
import br.com.qtota.data.remote.home_response.CategoryResponse
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.LocationComponent
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.components.ProductListItem
import br.com.qtota.ui.components.SearchTextField
import br.com.qtota.ui.state_handler.LoadMoreListState
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.GrayColor
import br.com.qtota.ui.theme.defaultPadding
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

@Composable
internal fun SearchProductScreen(navController: NavHostController, query: String?) {

    val viewModel: SearchProductViewModel = hiltViewModel()

    val categoryListState by viewModel.categoryListState.collectAsState()

    val listProductState by viewModel.productListState.collectAsState()
    val loadState by viewModel.loadState.collectAsState()

    val listState = rememberLazyListState()
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(query) {
        viewModel.performSearch(query)
    }

    LaunchedEffect(listState, listProductState.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { lastVisible ->
                if (lastVisible >= listProductState.size && loadState == LoadMoreListState.SUCCESS) {
                    viewModel.loadMore()
                }
            }
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

            item {
                when(categoryListState) {
                    is UIState.Loading -> Box(Modifier.fillMaxSize()) {
                        Text(
                            stringResource(R.string.loading_categories),
                            Modifier.align(Alignment.Center)
                        )
                    }
                    is UIState.Error -> Text("Não foi possível carregar as categorias")
                    is UIState.Success -> {
                        val categoryList = (categoryListState as UIState.Success).data
                        CategoryTabs(categoryList, selectedIndex) { index, category ->
                            selectedIndex = index
                            viewModel.selectTab(category)
                        }
                    }
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