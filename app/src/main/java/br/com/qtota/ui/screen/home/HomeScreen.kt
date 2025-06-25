package br.com.qtota.ui.screen.home

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
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
                "QtoTá?",
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
                    Scaffold(floatingActionButton = { ChatButton() }) {
                        val storeTabsState by viewModel.storeTabsState.collectAsState()
                        val mainState by viewModel.listProductState.collectAsState()
                        Content(storeTabsState, mainState, navController, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun Content(
    storeTabsState: List<String>,
    listProductState: ListProductState,
    navController: NavHostController,
    viewModel: HomeViewModel,
) {

    val savedProducts by viewModel.savedProducts.collectAsState()

    when (listProductState) {
        is ListProductState.Loading -> {
            Column {
                SearchContent(false, navController, viewModel)
                StoresTabs(storeTabsState)
                LoadingComponent()
            }
        }
        is ListProductState.Success -> {
            if (listProductState.products.isNotEmpty()) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    SearchContent(true, navController, viewModel)
                    StoresTabs(storeTabsState)
                    ProductList(
                        products = listProductState.products,
                        navController = navController,
                        savedProducts = savedProducts,
                        onHighlightedButtonClick = { viewModel.saveProduct(it) },
                    )
                }
            } else {
                Column {
                    SearchContent(true, navController, viewModel)
                    StoresTabs(storeTabsState)
                    MessageContent({ Icon(
                        painter = painterResource(R.drawable.ic_empty_shopping_cart),
                        contentDescription = null,
                        modifier = Modifier.size(96.dp),
                    ) }, "Não há nada aqui")
                }
            }
        }
        is ListProductState.Error -> {
            Column {
                SearchContent(false, navController, viewModel)
                StoresTabs(storeTabsState)
                ErrorComponent(listProductState.errorMessage)
            }
        }
    }
}

@Composable
private fun SearchContent(enabled: Boolean, navController: NavHostController, viewModel: HomeViewModel) {
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
            enabled = enabled,
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
        SendFlyerDialog { showSendFlyerDialog = false }
    }

}

@Composable
private fun StoresTabs(tabs: List<String>) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    ScrollableTabRow(
        edgePadding = 16.dp,
        selectedTabIndex = selectedIndex,
        indicator = {},
        divider = {}
    ) {
        Tab(
            modifier = if (selectedIndex == 0) Modifier
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(50))
                .background(DefaultColor)
            else Modifier
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(50))
                .background(GrayColor),
            onClick = { selectedIndex = 0 },
            selected = selectedIndex == 0,
            text = {
                Text(text = "Todos", color = if (selectedIndex == 0) Color.White else DefaultColor)
            }
        )

        tabs.forEachIndexed { index, text ->
            val selected = index == (selectedIndex - 1)
            Tab(
                modifier = if (selected) Modifier
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(DefaultColor)
                else Modifier
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(GrayColor),
                selected = selected,
                onClick = { selectedIndex = index + 1 },
                text = {
                    Text(text = text, color = if (selected) Color.White else DefaultColor)
                }
            )
        }
    }
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