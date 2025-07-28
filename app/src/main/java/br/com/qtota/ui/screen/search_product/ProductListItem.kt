package br.com.qtota.ui.screen.search_product

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.data.remote.store.StoreResponse
import br.com.qtota.ui.components.ConfirmDialog
import br.com.qtota.ui.components.ImageComponent
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.GrayColor
import br.com.qtota.ui.theme.ProductTitle
import br.com.qtota.ui.theme.defaultPadding
import br.com.qtota.utils.DateUtils.toDDMM
import br.com.qtota.utils.StringUtils.toDistanceString
import br.com.qtota.utils.StringUtils.toMonetaryString
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.Store
import java.time.LocalDate

@Composable
internal fun ProductListItem(
    product: ProductResponse,
    navController: NavHostController,
    viewModel: SearchProductViewModel,
    modifier: Modifier = Modifier
) {

    Card(
        modifier.clickable {
            navController.navigate(
                AppRoute.ProductDetails
                    .productId(product.id)
            )
        },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ImageComponent(product.urlImage, Lucide.ShoppingCart, 112.dp)
            Column {
                if(viewModel.store == null) {
                    StoreHeader(product.store)
                }
                InfoProductSection(product)
            }
        }
        HorizontalDivider(Modifier.padding(horizontal = defaultPadding), color = GrayColor)
        ButtonsSection(product, viewModel)
    }

}

@Composable
private fun StoreHeader(store: StoreResponse) {
    Row(
        modifier = Modifier
            .background(GrayColor)
            .padding(defaultPadding, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImageComponent(store.logo, Lucide.Store, 40.dp)
        Spacer(Modifier.width(defaultPadding))
        Column(Modifier.weight(1f)) {
            Text(
                store.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 15.sp,
                    color = Color.DarkGray,
                )
            )
            Text(
                store.branch,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 12.sp,
                    lineHeight = 13.sp,
                )
            )
        }
        Spacer(Modifier.width(defaultPadding))
        Row(
            Modifier
                .background(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFD3E2FD)
                )
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Lucide.MapPin,
                null,
                Modifier.size(16.dp),
                tint = Color(0xFF0015DF)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                store.distance.toDistanceString(),
                color = Color(0xFF0015DF),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun InfoProductSection(product: ProductResponse) {

    Column(Modifier.padding(defaultPadding)) {
        ProductTitle(product.name)
        Text(
            stringResource(
                R.string.valid_offer_until_date,
                product.expirationOffer.toDDMM()
            ),
            fontSize = 13.sp,
            color = Color.Gray,
        )
        Spacer(Modifier.height(6.dp))
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                product.price.toMonetaryString(),
                Modifier.padding(4.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF007700)
            )
            if(product.percentageOfAverage > 0) {
                Text(
                    "Economize ${product.percentageOfAverage}%",
                    Modifier
                        .background(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFF883C)
                        )
                        .padding(6.dp, 1.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color.White,
                )
            } else if(product.percentageOfAverage < 0) {
                Text(
                    "${-product.percentageOfAverage}% acima da média",
                    Modifier
                        .padding(6.dp, 1.dp),
                    fontSize = 12.sp,
                    color = Color.Red,
                )
            }
        }
    }

}

@Composable
private fun ButtonsSection(product: ProductResponse, viewModel: SearchProductViewModel) {

    val savedProductsState by viewModel.savedProductsState.collectAsState()
    val isSaved = savedProductsState.any { it.id == product.id }

    var dialog by remember { mutableStateOf<ProductResponse?>(null) }

    Row(Modifier
        .fillMaxWidth()
        .padding(8.dp),
        horizontalArrangement = Arrangement.Center) {
        Button({ dialog = product },
            Modifier.height(36.dp),
            contentPadding = PaddingValues(vertical = 0.dp, horizontal = defaultPadding),
            colors = ButtonDefaults.buttonColors(
                containerColor = DefaultColor,
                contentColor = Color.White
            ),
        ) {
            Text(
                stringResource(if (isSaved) R.string.delete_from_saved else R.string.save),
                fontSize = 13.sp
            )
        }
        Spacer(Modifier.width(defaultPadding))
        OutlinedButton({},
            Modifier.height(36.dp),
            contentPadding = PaddingValues(vertical = 0.dp, horizontal = defaultPadding),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = DefaultColor
            ),
            border = BorderStroke(1.dp, DefaultColor)
        ) {
            Text(stringResource(R.string.share), fontSize = 13.sp)
        }
    }

    dialog?.let {
        ConfirmDialog(
            text = if(isSaved) "Deseja remover este produto dos salvos?" else "Deseja salvar este produto?",
            onConfirm = {
                if (isSaved) viewModel.deleteProduct(it)
                else viewModel.saveProduct(it)
                dialog = null
            },
            onDismiss = { dialog = null }
        )
    }

}

@Composable @Preview(showBackground = true)
private fun ProductListItemPreview() {
    ProductListItem(
        product = ProductResponse(
            id = 0,
            name = "Name",
            expirationOffer = LocalDate.now(),
            price = 0.0,
            percentageOfAverage = 0,
            urlImage = null,
            store = StoreResponse(
                id = 0,
                name = "Store Name",
                latitude = 0.0,
                longitude = 0.0,
                branch = "Store Branch",
                distance = 0,
                logo = null
            ),
        ),
        navController = rememberNavController(),
        viewModel = hiltViewModel(),
    )
}
