package br.com.qtota.ui.screen.store_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.scale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.ImageComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.screen.home.HomeTextButton
import br.com.qtota.ui.screen.home.HomeTitle
import br.com.qtota.ui.screen.search_product.Store
import br.com.qtota.ui.screen.store_detail.model.StoreDetail
import br.com.qtota.ui.screen.store_detail.model.StoreDetailBranch
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.ProductTitle
import br.com.qtota.ui.theme.defaultPadding
import br.com.qtota.utils.BitmapUtils
import br.com.qtota.utils.BitmapUtils.cropToCircle
import br.com.qtota.utils.StringUtils.toDistanceString
import br.com.qtota.utils.Utils.mapStyle
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun StoreDetailScreen(navController: NavHostController, bottomNavController: NavHostController) {

    val viewModel: StoreDetailViewModel = hiltViewModel()

    val storeDetail by viewModel.storeDetailState.collectAsState()

    when(storeDetail) {
        is UIState.Loading -> {
            LoadingComponent(Modifier.fillMaxSize())
        }
        is UIState.Error -> {
            ErrorComponent(stringResource(R.string.error_loading_message), Modifier.fillMaxSize())
        }
        is UIState.Success -> {
            val store = (storeDetail as UIState.Success).data

            LazyColumn {
                item { StoreHeader(store) }
                item { FeaturedOffersSection(store, navController, bottomNavController) }
                item { NearestBranchSection(store, bottomNavController) }
                if(store.branchList.size > 1) {
                    item {
                        OtherBranchesSection(store.branchList.drop(1))
                    }
                }
            }
        }
    }

}

@Composable
private fun StoreHeader(store: StoreDetail) {
    Row(
        Modifier
            .padding(defaultPadding)
            .fillMaxWidth()
            .background(Color.White)
            .padding(defaultPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImageComponent(
            store.urlLogo,
            errorImageRes = R.drawable.outline_store_24,
            size = 96.dp
        )
        Spacer(Modifier.width(defaultPadding))
        Column {
            ProductTitle(store.name)
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(R.drawable.outline_store_24),
                    null,
                    Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    pluralStringResource(
                        R.plurals.branch_in_your_region,
                        store.branchList.size,
                        formatArgs = arrayOf(store.branchList.size)
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                    )
                )
            }

        }
    }
}

@Composable
private fun FeaturedOffersSection(store: StoreDetail, navController: NavHostController, bottomNavController: NavHostController) {

    val screenWidth = LocalWindowInfo.current.containerSize.width
    val itemWidth = (0.15f * screenWidth).dp

    Column(Modifier.fillMaxWidth()) {
        HomeTitle(stringResource(R.string.featured_offers), Modifier.padding(defaultPadding))
        if(store.products.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Max)
                    .horizontalScroll(rememberScrollState())
            ) {
                store.products.forEachIndexed { index, product ->
                    StoreDetailProductItem(
                        product = product,
                        navController = navController,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(
                                start = if (index == 0) defaultPadding else defaultPadding / 2,
                                end = if (index == store.products.size - 1) defaultPadding else defaultPadding / 2
                            )
                            .width(itemWidth)
                    )
                }
            }
            HomeTextButton(
                stringResource(R.string.see_more_offers), Modifier
                    .align(Alignment.End)
                    .padding(horizontal = defaultPadding)
            ) {
                bottomNavController.navigate(
                    AppRoute.SearchProduct.createRoute(store = Store(
                        id = store.id,
                        name = store.name,
                        urlLogo = store.urlLogo
                    ))
                )
            }
        } else {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
            )
        }
    }
}

@Composable
private fun NearestBranchSection(store: StoreDetail, bottomNavController: NavHostController) {

    val nearbyBranch = store.branchList[0]

    Column(
        Modifier
            .padding(defaultPadding)
            .fillMaxWidth()
            .background(Color.White)
    ) {
        HomeTitle(stringResource(R.string.nearest_branch), Modifier.padding(defaultPadding))
        HorizontalDivider(thickness = 0.5.dp)
        Column(
            Modifier.padding(defaultPadding)
        ) {
            MapContainer(store, Modifier.padding(vertical = defaultPadding))
            Text(
                nearbyBranch.description,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                color = Color.DarkGray
            )
            Text(
                nearbyBranch.address,
                Modifier.padding(vertical = 4.dp),
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 15.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.LocationOn,
                    null,
                    Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    nearbyBranch.distance.toDistanceString(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(Modifier.height(defaultPadding))
            Button(
                {
                    bottomNavController.navigate(AppRoute.Map.createRoute(store.id))
                },
                Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DefaultColor
                )
            ) {
                Text(stringResource(R.string.see_all_branches))
            }
        }
    }

}

@Composable
private fun MapContainer(store: StoreDetail, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val nearbyBranch = store.branchList[0]

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(nearbyBranch.position, 15f)
    }

    val targetSize = 40.dp
    val iconSizePx = with(LocalDensity.current) { targetSize.toPx().toInt() }

    var markerIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    LaunchedEffect(store.urlLogo) {
        BitmapUtils.downloadImageFromUrl(store.urlLogo, context)?.let { bitmap ->
            val circleBmp = bitmap.cropToCircle()
            val scaledBmp = circleBmp.scale(iconSizePx, iconSizePx, false)
            markerIcon = BitmapDescriptorFactory.fromBitmap(scaledBmp)
        }
    }

    val marker = remember { MarkerState(position = nearbyBranch.position )}

    GoogleMap(
        modifier.height(200.dp),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapStyleOptions = MapStyleOptions(mapStyle),
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false,
            tiltGesturesEnabled = false,
            zoomGesturesEnabled = false,
            scrollGesturesEnabled = false,
            rotationGesturesEnabled = false,
            scrollGesturesEnabledDuringRotateOrZoom = false
        )
    ) {
        Marker(
            state = marker,
            icon = markerIcon,
        )
    }

}

@Composable
private fun OtherBranchesSection(branches: List<StoreDetailBranch>) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = defaultPadding)
            .background(Color.White)
    ) {
        HomeTitle(stringResource(R.string.other_nearest_branches), Modifier.padding(defaultPadding))
        HorizontalDivider(thickness = 0.5.dp)
        Column(Modifier.padding(defaultPadding)) {
            branches.drop(1).forEachIndexed { index, branch ->
                Column(
                    Modifier.padding(
                        start = defaultPadding, end = defaultPadding,
                        top = if (index == 0) defaultPadding / 2 else defaultPadding,
                        bottom = if (index == branches.size - 1) defaultPadding / 2 else defaultPadding
                    )
                ) {
                    Text(
                        branch.description,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        branch.address,
                        Modifier.padding(vertical = 4.dp),
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        lineHeight = 15.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            null,
                            Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            branch.distance.toDistanceString(),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                if (index < branches.size - 2) {
                    HorizontalDivider(thickness = 0.25.dp)
                }
            }
        }
    }
    Spacer(Modifier.height(defaultPadding))
}

@Composable @Preview(showBackground = true)
private fun StoreDetailScreenPreview() {
    StoreDetailScreen(rememberNavController(), rememberNavController())
}