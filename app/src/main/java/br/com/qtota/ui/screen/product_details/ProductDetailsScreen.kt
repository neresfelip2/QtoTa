package br.com.qtota.ui.screen.product_details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.data.remote.product.MeasureType
import br.com.qtota.data.remote.product.ProductStoreResponse
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.ui.components.ConfirmDialog
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.Toolbar
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.GradientBackground
import br.com.qtota.ui.theme.GrayColor
import br.com.qtota.ui.theme.defaultPadding
import br.com.qtota.utils.StringUtils.stringDaysAfterNow
import br.com.qtota.utils.StringUtils.toDistanceString
import br.com.qtota.utils.StringUtils.toMeasureString
import br.com.qtota.utils.StringUtils.toMonetaryString
import coil.compose.AsyncImage

@Composable
internal fun ProductDetailsScreen(navController: NavHostController) {

    val viewModel: ProductDetailsViewModel = hiltViewModel()

    val savedProductState by viewModel.savedProductState.collectAsState()
    val productState by viewModel.productDetails.collectAsState()

    var showSaveProductDialog by remember { mutableStateOf(false) }
    var showDeleteProductDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Toolbar(
                title = null,
                backButtonEnabled = navController,
                *if (savedProductState is UIState.Success) {
                    arrayOf(
                        if ((savedProductState as UIState.Success).data)
                            Icons.Outlined.Favorite to { showDeleteProductDialog = true }
                        else
                            Icons.Outlined.FavoriteBorder to { showSaveProductDialog = true },

                        Icons.Outlined.Share to { }
                    )
                } else {
                    arrayOf()
                }
            )
        }
    ) { innerPadding ->

        when(productState) {
            is UIState.Loading -> LoadingComponent(Modifier.fillMaxSize())
            is UIState.Error -> ErrorComponent(
                (productState as UIState.Error).description,
                Modifier.fillMaxSize()
            )
            is UIState.Success -> ContainerSuccess(innerPadding, (productState as UIState.Success).data)
        }

        if(showSaveProductDialog) {
            ConfirmDialog(
                text = stringResource(R.string.save_product_dialog),
                onConfirm = {
                    viewModel.saveProduct()
                    showSaveProductDialog = false
                },
                onDismiss = {
                    showSaveProductDialog = false
                }
            )
        }

        if(showDeleteProductDialog) {
            ConfirmDialog(
                text = stringResource(R.string.delete_product_dialog),
                onConfirm = {
                    viewModel.deleteProduct()
                    showDeleteProductDialog = false
                },
                onDismiss = {
                    showDeleteProductDialog = false
                }
            )
        }


    }
}

@Composable
private fun ContainerSuccess(innerPadding: PaddingValues, product: ProductDetail) {
    Column(Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(defaultPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        product.urlImage?.let {
            AsyncImage(
                model = it,
                contentDescription = "Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Inside
            )
        } ?: Image(
            painterResource(R.drawable.outline_photo_24),
            null,
            Modifier.height(160.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(defaultPadding))
        Text(product.name, fontWeight = FontWeight.Bold)
        Text(product.description, color = Color.Gray, fontSize = 12.sp)
        Spacer(Modifier.height(defaultPadding))
        Column(
            Modifier
                .fillMaxWidth()
                .background(
                    GradientBackground,
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.best_price), color = Color.White)
                    Text(product.bestPrice.toMonetaryString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column {
                    Text(stringResource(R.string.variation), Modifier.align(Alignment.End), color = Color.White)
                    Text("${product.bestPrice.toMonetaryString()} - ${product.highestPrice.toMonetaryString()}", color = Color.White)
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                {},
                Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White,
                    containerColor = Color(0x30FFFFFF)
                ),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(stringResource(R.string.create_price_alert))
            }
        }

        Spacer(Modifier.height(defaultPadding * 2))

        var selectedTab by remember { mutableStateOf(Tab.TAB_PRICES) }
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            indicator = {},
            divider = {},
        ) {
            TabItem(Tab.TAB_PRICES, selectedTab) { selectedTab = it }
            TabItem(Tab.TAB_DETAILS, selectedTab) { selectedTab = it }
        }

        Spacer(Modifier.height(defaultPadding))

        when(selectedTab) {
            Tab.TAB_PRICES -> PricesContainer(product.stores)
            Tab.TAB_DETAILS -> DetailsContainer(
                measure = product.weight,
                type = product.type,
                measureType = product.measureType,
                origin = product.origin,
                expiration = product.expiration
            )
        }

    }
}
@Composable
private fun TabItem(tab: Tab, selectedTab: Tab, onClick: (Tab) -> Unit) {
    Tab(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(50))
            .background(if (selectedTab == tab) DefaultColor else GrayColor),
        selected = selectedTab == tab,
        onClick = { onClick(tab) },
        text = {
            Text(text = tab.label, color = if (selectedTab == tab) Color.White else DefaultColor)
        }
    )
}

@Composable
private fun PricesContainer(stores: List<ProductStoreResponse>) {
    Column {
        stores.forEachIndexed { index, store ->

            Column(
                Modifier
                    .padding(vertical = 8.dp)
                    .border(
                        BorderStroke(1.dp, DefaultColor),
                        RoundedCornerShape(16.dp),
                    )
            ) {

                if(index == 0) {
                    Text(
                        stringResource(R.string.best_price),
                        Modifier
                            .align(Alignment.End)
                            .background(
                                DefaultColor,
                                RoundedCornerShape(topEnd = 16.dp)
                            )
                            .padding(4.dp),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Spacer(Modifier.height(defaultPadding))
                }

                Column(Modifier.padding(horizontal = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        store.logo?.let { logo ->
                            AsyncImage(
                                model = logo,
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(MaterialTheme.shapes.small),
                                contentScale = ContentScale.Inside
                            )
                        } ?:
                        Image(
                            painterResource(R.drawable.outline_photo_24),
                            null,
                            Modifier.size(48.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                store.name,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Outlined.Place,
                                    null,
                                    Modifier.size(16.dp),
                                    tint = Color.Gray
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(store.distance.toDistanceString(), color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                        Text(
                            store.currentPrice.toMonetaryString(),
                            color = Color.DarkGray,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider()
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(
                            Modifier.padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painterResource(R.drawable.outline_nest_clock_farsight_analog_24),
                                null,
                                Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                store.expirationOffer.stringDaysAfterNow(LocalContext.current),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        TextButton({},
                            colors = ButtonDefaults.textButtonColors(contentColor = DefaultColor),
                            contentPadding = PaddingValues(vertical = 0.dp, horizontal = 8.dp)
                        ) {
                            Icon(painterResource(R.drawable.outline_flag_2_24), null, Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.report), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsContainer(measure: Int, measureType: MeasureType, type: String, origin: String, expiration: Int) {
    Column(Modifier
        .background(
            Color.White,
            RoundedCornerShape(defaultPadding)
        )
        .padding(defaultPadding),
    ) {
        Text(stringResource(R.string.information), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(defaultPadding))
        DetailRow(measureType.label,measure.toMeasureString(measureType))
        HorizontalDivider()
        DetailRow(stringResource(R.string.type), type)
        HorizontalDivider()
        DetailRow(stringResource(R.string.origin), origin)
        HorizontalDivider()
        DetailRow(stringResource(R.string.days_until_expiration), stringResource(R.string.expiration_days, expiration))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.DarkGray, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Bold, color = Color.DarkGray, fontSize = 14.sp)
    }
}

enum class Tab(val label: String) {
    TAB_PRICES("Preços"), TAB_DETAILS("Detalhes")
}

@Composable @Preview(showSystemUi = true)
private fun ProductDetailsScreenPreview() {
    ProductDetailsScreen(rememberNavController())
}
