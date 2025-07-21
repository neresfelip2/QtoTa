package br.com.qtota.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.data.remote.store.StoreResponse
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.GrayColor
import br.com.qtota.ui.theme.ProductTitle
import br.com.qtota.ui.theme.defaultPadding
import br.com.qtota.utils.DateUtils.toDDMM
import br.com.qtota.utils.StringUtils.toDistanceString
import br.com.qtota.utils.StringUtils.toMonetaryString
import coil.compose.AsyncImage
import java.time.LocalDate

@Composable
internal fun ProductListItem(
    product: ProductResponse,
    navController: NavHostController,
    onHighlightedButtonClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var saveProduct by remember { mutableStateOf<Product?>(null) }
    var deleteProduct by remember { mutableStateOf<Product?>(null) }

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
            if (product.urlImage.isNullOrBlank()) {
                Icon(
                    Icons.Outlined.ShoppingCart,
                    null,
                    Modifier.size(112.dp),
                    tint = Color.LightGray
                )
            } else {
                AsyncImage(
                    product.urlImage,
                    null,
                    Modifier.size(112.dp),
                    contentScale = ContentScale.Inside
                )
            }
            Column {
                Row(
                    modifier = Modifier
                        .background(GrayColor)
                        .padding(defaultPadding, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (product.store.logo != null && product.store.logo.isNotEmpty()) {
                        AsyncImage(
                            model = product.store.logo,
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(MaterialTheme.shapes.small),
                            contentScale = ContentScale.Inside
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.outline_store_24),
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier
                                .size(40.dp)
                        )
                    }
                    Spacer(Modifier.width(defaultPadding))
                    Column(Modifier.weight(1f)) {
                        Text(product.store.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 15.sp,
                                color = Color.DarkGray,
                            )
                        )
                        Text(product.store.branch,
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
                        Icon(Icons.Outlined.LocationOn, null, Modifier.size(16.dp), tint = Color(0xFF0015DF))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            product.store.distance.toDistanceString(),
                            color = Color(0xFF0015DF),
                            fontSize = 12.sp
                        )
                    }
                }
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
                        Text(
                            "Economize ${product.percentageOfAverage}%",
                            Modifier
                                .background(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFF883C)
                                )
                                .padding(8.dp, 2.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Color.White,
                        )
                    }
                }
            }
        }
        HorizontalDivider(Modifier.padding(horizontal = defaultPadding), color = GrayColor)
        Row(Modifier
            .fillMaxWidth()
            .padding(8.dp),
            horizontalArrangement = Arrangement.Center) {
            Button({},
                Modifier.height(36.dp),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = defaultPadding),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DefaultColor,
                    contentColor = Color.White
                ),
            ) {
                Text(stringResource(R.string.save), fontSize = 13.sp)
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
    }

    saveProduct?.let {
        ConfirmDialog(
            text = "Deseja salvar este produto?",
            onConfirm = {
                onHighlightedButtonClick(it)
                saveProduct = null
            },
            onDismiss = { saveProduct = null }
        )
    }

    deleteProduct?.let {
        ConfirmDialog(
            text = "Deseja remover este produto dos salvos?",
            onConfirm = {
                onHighlightedButtonClick(it)
                deleteProduct = null
            },
            onDismiss = { deleteProduct = null }
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
                branch = "Store Branch",
                distance = 0,
                logo = null
            ),
        ),
        navController = rememberNavController(),
        onHighlightedButtonClick = {}
    )
}
