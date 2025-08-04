package br.com.qtota.ui.screen.search_product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
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
import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.ImageComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.LocationComponent
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.components.SearchTextField
import br.com.qtota.ui.screen.home.HomeTitle
import br.com.qtota.ui.state_handler.LoadMoreListState
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.GrayColor
import br.com.qtota.ui.theme.defaultPadding
import com.composables.icons.lucide.LayoutGrid
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Store
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

@Composable
internal fun SearchProductScreen(navController: NavHostController) {

    val viewModel: SearchProductViewModel = hiltViewModel()

    val neighborhood by viewModel.neighborhood.collectAsState()
    val categoryListState by viewModel.categoryListState.collectAsState()

    val listProductState by viewModel.productListState.collectAsState()
    val loadState by viewModel.loadState.collectAsState()

    val listState = rememberLazyListState()

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
                    neighborhood,
                    Modifier.padding(start = defaultPadding, top = defaultPadding)
                )
            }

            viewModel.store?.let { store ->
                item { StoreSection(store) }
            }

            item {
                SearchTextField(
                    Modifier
                        .fillMaxWidth()
                        .padding(defaultPadding),
                    viewModel.query,
                    viewModel::performSearch
                )
            }

            stickyHeader {
                CategorySection(categoryListState, viewModel)
            }

            itemsIndexed(listProductState) { index, product ->
                ProductListItem(
                    product,
                    navController,
                    viewModel,
                    Modifier.padding(
                        start = defaultPadding, end = defaultPadding,
                        top = if(index == 0) defaultPadding else defaultPadding/2,
                        bottom = if(index == listProductState.size - 1) defaultPadding else defaultPadding/2
                    )
                )
            }

            endListSection(listProductState, loadState)

        }

        EmptyListSection(listProductState, loadState)

    }

}

private fun LazyListScope.endListSection(
    listProductState: List<ProductResponse>,
    loadState: LoadMoreListState,
) {
    if (listProductState.isNotEmpty()) {

        if (loadState == LoadMoreListState.LOADING) {
            item {
                Box(Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(
                        Modifier
                            .padding(defaultPadding)
                            .align(Alignment.Center)
                    )
                }
            }
            return
        } else if (loadState == LoadMoreListState.EMPTY) {
            item {
                Text(
                    "Fim da lista",
                    Modifier
                        .fillMaxWidth()
                        .padding(defaultPadding),
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}

@Composable
private fun StoreSection(store: Store) {
    Row(Modifier.padding(defaultPadding), verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(R.string.you_are_seeing_offers_in), fontSize = 13.sp, color = Color.DarkGray)
        Spacer(Modifier.width(defaultPadding))
        ImageComponent(store.urlLogo, Lucide.Store, 24.dp)
        Spacer(Modifier.width(defaultPadding))
        HomeTitle(store.name)
    }
}

@Composable
private fun CategorySection(categoryListState: UIState<List<CategoryResponse>>, viewModel: SearchProductViewModel) {

    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    when(categoryListState) {
        is UIState.Loading -> Box(Modifier.fillMaxSize()) {
            Row(Modifier
                .padding(defaultPadding)
                .fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator(color = DefaultColor)
                Spacer(Modifier.width(defaultPadding))
                Text(
                    stringResource(R.string.loading_categories),
                    color = DefaultColor
                )
            }
        }
        is UIState.Error -> Text("Não foi possível carregar as categorias")
        is UIState.Success -> {
            CategoryTabs(categoryListState.data, selectedIndex) { index, category ->
                selectedIndex = index
                viewModel.selectTab(category)
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
            ImageComponent(
                urlIcon,
                Lucide.LayoutGrid,
                24.dp,
                if(selected) Color.White else DefaultColor
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
private fun EmptyListSection(
    listProductState: List<ProductResponse>,
    loadState: LoadMoreListState
) {

    if(listProductState.isEmpty()) {
        when (loadState) {
            LoadMoreListState.LOADING -> {
                LoadingComponent(
                    Modifier
                        .fillMaxSize()
                        .padding(defaultPadding)
                )
            }
            LoadMoreListState.ERROR -> {
                ErrorComponent(
                    stringResource(R.string.error_loading_message),
                    Modifier.fillMaxSize()
                )
            }
            LoadMoreListState.EMPTY -> {
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

            LoadMoreListState.SUCCESS -> {}
        }
    }

}