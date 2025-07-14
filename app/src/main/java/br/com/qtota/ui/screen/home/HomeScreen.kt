package br.com.qtota.ui.screen.home

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.data.remote.home_response.CategoryResponse
import br.com.qtota.ui.UIState
import br.com.qtota.ui.components.ConfirmDialog
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.components.ProductList
import br.com.qtota.ui.components.Toolbar
import br.com.qtota.ui.navigation.AppRoutes
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.GrayColor
import br.com.qtota.ui.theme.defaultPadding
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun HomeScreen(navController: NavHostController) {

    val viewModel: HomeViewModel = hiltViewModel()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BackHandler(drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Toolbar(
                stringResource(R.string.app_name),
                backButtonEnabled = null,
                Icons.Outlined.Notifications to {
                    scope.launch {
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                },
                Icons.Outlined.Settings to {
                    navController.navigate(AppRoutes.Settings.route) { launchSingleTop = true }
                })
        },
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ModalNavigationDrawer(
                modifier = Modifier.padding(innerPadding),
                drawerState = drawerState,
                drawerContent = { DrawerContent() },
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Scaffold/*(*floatingActionButton = { ChatButton() })*/ {
                        LocationStateHandler(navController, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationStateHandler(
    navController: NavHostController,
    viewModel: HomeViewModel,
) {
    val locationUIState by viewModel.locationUiState.collectAsState()

    when(locationUIState) {
        is UIState.Loading -> LoadingComponent(Modifier.fillMaxSize())
        is UIState.Error -> RequestLocationComponent(viewModel)
        is UIState.Success -> Content(navController, viewModel)
    }
}

@Composable
private fun Content(
    navController: NavHostController,
    viewModel: HomeViewModel,
) {

    val homeUIState by viewModel.homeUIState.collectAsState()
    val productListState by viewModel.productListState.collectAsState()
    val localityNameState by viewModel.localityNameState.collectAsState()

    when (homeUIState) {
        is UIState.Loading -> LoadingComponent(Modifier.fillMaxSize())
        is UIState.Error -> ErrorComponent(stringResource(R.string.error_loading_message), Modifier.fillMaxSize())
        is UIState.Success -> {

            val data = (homeUIState as UIState.Success).data

            var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
            val listState = rememberLazyListState()

            Column {
                LazyColumn(state = listState) {

                    item {
                        TextButton(
                            {},
                            Modifier
                                .padding(horizontal = defaultPadding)
                                .padding(top = defaultPadding),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = DefaultColor,
                                containerColor = Color.Transparent
                            )
                        ) {
                            Icon(Icons.Outlined.LocationOn, null, tint = DefaultColor)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                localityNameState,
                                fontWeight = FontWeight.Bold,
                                color = DefaultColor,
                                fontSize = 16.sp
                            )
                        }
                    }

                    item { SearchContent(navController, viewModel) }

                    item {
                        Text(
                            stringResource(R.string.cheapests_in_your_area),
                            Modifier.padding(defaultPadding),
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }

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
                                            onClick = { /*…*/ },
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
                    }

                    item {
                        Text(
                            "Lojas mais próximas",
                            Modifier.padding(defaultPadding),
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }

                    item {

                        if(data.nearbyStores.isEmpty()) {
                            MessageContent({
                                Icon(painterResource(R.drawable.outline_store_24), null,
                                    Modifier.size(128.dp),
                                    tint = Color(0x59187270)
                                )
                            }, stringResource(R.string.not_store_found),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp)
                            )
                            return@item
                        }

                        LazyRow(contentPadding = PaddingValues(8.dp)) {
                            items(data.nearbyStores) { store ->
                                Card(
                                    {},
                                    Modifier.padding(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Column(
                                        Modifier.padding(defaultPadding),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        store.logo?.let {
                                            AsyncImage(
                                                model = it,
                                                contentDescription = null,
                                                contentScale = ContentScale.Inside,
                                                modifier = Modifier.size(96.dp),
                                            )
                                        } ?: Icon(
                                            painterResource(R.drawable.outline_store_24), null,
                                            Modifier.size(96.dp),
                                            tint = Color.LightGray
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(store.name, fontSize = 14.sp, maxLines = 2)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextButton(
                                onClick = { /*…*/ },
                                Modifier.padding(horizontal = defaultPadding),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = DefaultColor,
                                    containerColor = Color.Transparent
                                )
                            ) {
                                Text(
                                    stringResource(R.string.see_more_stores),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            TextButton(
                                onClick = { /*…*/ },
                                Modifier
                                    .padding(horizontal = defaultPadding)
                                    .padding(bottom = defaultPadding),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = DefaultColor,
                                    containerColor = Color.Transparent
                                )
                            ) {
                                Text(stringResource(R.string.see_maps), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }

                }

            }
        }
    }
}

@Composable
private fun RequestLocationComponent(viewModel: HomeViewModel) {
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.requestLocation()
        }
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        MessageContent(
            { Icons.Outlined.LocationOn },
            stringResource(R.string.request_location_label)
        )
        Spacer(Modifier.height(defaultPadding))
        Button({
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }) {
            Text(stringResource(R.string.request_location_button))
        }
    }
}

@Composable
private fun SearchContent(navController: NavHostController, viewModel: HomeViewModel) {
    var text by rememberSaveable { mutableStateOf("") }

    var showLoginDialog by remember { mutableStateOf(false) }
    var showSendFlyerDialog by remember { mutableStateOf(false) }

    Row(
        Modifier.padding(defaultPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text(stringResource(R.string.search_products)) },
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = DefaultColor,
                unfocusedPlaceholderColor = Color.Gray
            ),
            shape = CircleShape,
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(defaultPadding))

        IconButton(
            {
                viewModel.checkIfLogged { isLogged ->
                    if(isLogged) {
                        showSendFlyerDialog = true
                    } else {
                        showLoginDialog = true
                    }
                }
            },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Send,
                contentDescription = "",
                tint = DefaultColor
            )
        }
    }

    if(showLoginDialog) {
        ConfirmDialog(
            text = stringResource(R.string.send_flyer_dialog),
            onDismiss = { showLoginDialog = false },
            onConfirm = {
                showLoginDialog = false
                navController.navigate(AppRoutes.Login.route)
            },
            confirmText = stringResource(R.string.log_in)
        )
    }

    if(showSendFlyerDialog) {
        SendFlyerDialog(viewModel) { showSendFlyerDialog = false }
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

        StoreTabsItem(stringResource(R.string.all), null, selectedIndex == 0) {
            onClickTab(0, null)
        }

        tabs.forEachIndexed { index, category ->
            StoreTabsItem(category.name, category.urlIcon, index == (selectedIndex - 1)) {
                onClickTab(index + 1, category)
            }
        }

    }
}

@Composable
private fun StoreTabsItem(name: String, urlIcon: String?, selected: Boolean, onClick: () -> Unit) {
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
                    fontSize = 12.sp,
                )
            }
        }
    )
}

@Composable
private fun ChatButton() {
    FloatingActionButton(
        {},
        shape = CircleShape,
        containerColor = DefaultColor
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_chat_24),
            tint = Color.White,
            contentDescription = null,
        )
    }
}

@Composable @Preview
private fun DrawerContent() {
    Column(
        Modifier
            .padding(end = 80.dp)
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(defaultPadding)
    ) {
        /*Text("Notificações", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        HorizontalDivider()
        Text("Você tem 3 cupons pendentes")
        Text("Nova oferta: 20% OFF")*/
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}

