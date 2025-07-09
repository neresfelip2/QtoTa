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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.data.remote.store_tabs.TabItem
import br.com.qtota.ui.SendFlyerDialog
import br.com.qtota.ui.components.ConfirmDialog
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.components.ProductList
import br.com.qtota.ui.components.Toolbar
import br.com.qtota.ui.navigation.AppRoutes
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.GrayColor
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
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
                    Scaffold(/*floatingActionButton = { ChatButton() }*/) {
                        Content(navController, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun Content(
    navController: NavHostController,
    viewModel: HomeViewModel,
) {

    val storeTabsState by viewModel.storeTabsState.collectAsState()
    val listProductState by viewModel.productListState.collectAsState()
    val loadListState by viewModel.loadListState.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.requestLocation()
        }
    }

    if(loadListState == LoadState.LocationError) {
        Column(Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center)
        {
            MessageContent(
                { Icons.Outlined.LocationOn },
                stringResource(R.string.request_location_label)
            )
            Button({
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }) {
                Text(stringResource(R.string.request_location_button))
            }
        }
        return
    }

    if (loadListState == LoadState.LoadingScreen) {
        LoadingComponent(Modifier.fillMaxSize())
        return
    }

    val listState = rememberLazyListState()
    LaunchedEffect(listState, listProductState.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { lastVisible ->
                if (lastVisible >= listProductState.size && (loadListState == LoadState.ReadyToLoad)) {
                    viewModel.loadMoreProducts()
                }
            }
    }

    Column {
        LazyColumn(state = listState) {

            item { SearchContent(navController, viewModel) }
            stickyHeader {
                StoreTabs(storeTabsState) { storeName ->
                    viewModel.selectTab(storeName)
                }
            }

            if (loadListState == LoadState.LoadingAllList) {
                return@LazyColumn
            }

            if (listProductState.isEmpty()) {
                item {
                    ErrorComponent("Algo deu errado")
                }
                return@LazyColumn
            }

            items(listProductState) { product ->
                ProductList(
                    product = product,
                    navController = navController,
                    onHighlightedButtonClick = {
                        viewModel.saveProduct(product)
                    },
                    location = viewModel.location
                )
            }

            if (loadListState == LoadState.LoadingMore) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
            } else if (loadListState == LoadState.FinalList) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Sem mais produtos a exibir", Modifier.align(Alignment.Center))
                    }
                }
            }

        }

        if (loadListState == LoadState.LoadingAllList) {
            LoadingComponent(Modifier.fillMaxSize())
        }
    }

}

@Composable
private fun SearchContent(navController: NavHostController, viewModel: HomeViewModel) {
    var text by remember { mutableStateOf("") }

    var showLoginDialog by remember { mutableStateOf(false) }
    var showSendFlyerDialog by remember { mutableStateOf(false) }

    Row(
        Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Pesquisar") },
            placeholder = { Text("Escreva aqui...") },
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DefaultColor,
                focusedLeadingIconColor = DefaultColor,
                focusedLabelColor = DefaultColor,
                focusedPlaceholderColor = Color.LightGray
            ),
            shape = CircleShape,
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
        )

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
            Modifier.padding(8.dp)
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
            text = "Para enviar um encarte, é necessário fazer login",
            onDismiss = { showLoginDialog = false },
            onConfirm = {
                showLoginDialog = false
                navController.navigate(AppRoutes.Login.route)
            },
            confirmText = "Fazer login"
        )
    }

    if(showSendFlyerDialog) {
        SendFlyerDialog(viewModel) { showSendFlyerDialog = false }
    }

}

@Composable
private fun StoreTabs(tabs: List<TabItem>, onClickTab: (TabItem?) -> Unit) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    ScrollableTabRow(
        edgePadding = 16.dp,
        selectedTabIndex = selectedIndex,
        indicator = {},
        divider = {}
    ) {

        StoreTabsItem("Todos", selectedIndex == 0) {
            selectedIndex = 0
            onClickTab(null)
        }

        tabs.forEachIndexed { index, tabItem ->
            StoreTabsItem(tabItem.storeName, index == (selectedIndex - 1)) {
                selectedIndex = index + 1
                onClickTab(tabItem)
            }
        }
    }
}

@Composable
private fun StoreTabsItem(name: String, selected: Boolean, onClick: () -> Unit) {
    Tab(
        modifier = if (selected) Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(50))
            .background(DefaultColor)
        else Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(50))
            .background(GrayColor),
        onClick = onClick,
        selected = selected,
        text = {
            Text(text = name, color = if (selected) Color.White else DefaultColor)
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Notificações", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        HorizontalDivider()
        Text("Você tem 3 cupons pendentes")
        Text("Nova oferta: 20% OFF")
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}

