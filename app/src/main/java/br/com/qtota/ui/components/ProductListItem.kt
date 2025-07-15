package br.com.qtota.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.com.qtota.data.local.entity.Product
import br.com.qtota.ui.navigation.AppRoutes
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.GrayColor
import br.com.qtota.ui.theme.defaultPadding
import br.com.qtota.utils.StringUtils.toDistanceString
import br.com.qtota.utils.StringUtils.toMonetaryString
import coil.compose.AsyncImage

@Composable
internal fun ProductListItem(
    product: Product,
    navController: NavHostController,
    onHighlightedButtonClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var saveProduct by remember { mutableStateOf<Product?>(null) }
    var deleteProduct by remember { mutableStateOf<Product?>(null) }

    Card(
        modifier.clickable {
                navController.navigate(
                    AppRoutes.ProductDetails
                        .productId(product.id)
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        Row(
            Modifier
                .background(GrayColor)
                .padding(defaultPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(product.logo != null && product.logo.isNotEmpty()) {
                AsyncImage(
                    model = product.logo,
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Inside
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(48.dp)
                )
            }
            Spacer(Modifier.width(defaultPadding))
            Column(Modifier.weight(1f)) {
                Text(product.storeName, color = Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(product.storeBranch, color = Color.Gray, fontSize = 14.sp, maxLines = 1)
            }
            Spacer(Modifier.width(defaultPadding))
            Text(
                product.distance.toDistanceString(),
                Modifier
                    .background(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFD3E2FD)
                    )
                    .padding(4.dp),
                color = Color(0xFF0015DF),
                fontSize = 14.sp
            )
        }

        Column(Modifier.padding(defaultPadding)) {
            Text(
                product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.DarkGray
            )
            Text(
                product.description,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    product.currentPrice.toMonetaryString(),
                    Modifier.padding(4.dp),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF007700)
                )

                Text(
                    "${product.discountPercentage}% abaixo da média",
                    Modifier
                        .background(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFF883C)
                        )
                        .padding(8.dp, 4.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White,
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                if(!product.isSaved) {
                    Button(
                        { saveProduct = product },
                        Modifier.padding(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = DefaultColor,
                        )
                    ) { Text("Salvar") }
                } else {
                    Button(
                        { deleteProduct = product },
                        Modifier.padding(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = DefaultColor,
                        )
                    ) { Text("Remover dos salvos") }
                }

                OutlinedButton(
                    {},
                    Modifier.padding(4.dp),
                    border = BorderStroke(1.dp, DefaultColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DefaultColor,
                    )
                ) { Text("Compartilhar") }

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
