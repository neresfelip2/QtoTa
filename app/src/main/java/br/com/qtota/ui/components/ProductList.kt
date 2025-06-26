package br.com.qtota.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.mapper.ProductMapper.calculateDiscount
import br.com.qtota.ui.navigation.AppRoutes
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.GrayColor
import br.com.qtota.utils.StringUtils.toDDMM
import br.com.qtota.utils.StringUtils.toDistanceString
import br.com.qtota.utils.StringUtils.toMonetaryString

@Composable
internal fun ProductList(
    product: Product,
    navController: NavHostController,
    onHighlightedButtonClick: (Product) -> Unit,
) {
    var saveProduct by remember { mutableStateOf<Product?>(null) }
    var deleteProduct by remember { mutableStateOf<Product?>(null) }

    Card(
        Modifier
            .padding(8.dp)
            .clickable {
                navController.navigate(
                    AppRoutes.ProductDetails.productId(product.id)
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        Row(
            Modifier
                .background(GrayColor)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier
                    .padding(8.dp)
                    .size(48.dp)
            )
            Column(
                Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text(product.storeName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Válido até ${product.expirationOffer.toDDMM()}", color = Color.DarkGray)
            }
            Text(
                product.distance.toDistanceString(),
                Modifier
                    .padding(8.dp)
                    .background(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFD3E2FD)
                    )
                    .padding(8.dp),
                color = Color(0xFF0015DF)
            )
        }
        Column(Modifier.padding(8.dp)) {
            Text(
                product.name,
                Modifier.padding(4.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                product.description,
                Modifier.padding(4.dp),
                color = Color.DarkGray
            )
            Row(
                Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    product.currentValue.toMonetaryString(),
                    Modifier.padding(4.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF007700)
                )
                product.previousValue?.let { previousBestPrice ->
                    Row(Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            previousBestPrice.toMonetaryString(),
                            Modifier.padding(8.dp),
                            color = Color.DarkGray,
                            style = TextStyle(
                                textDecoration = TextDecoration.LineThrough
                            )
                        )

                        Text(
                            "${product.calculateDiscount()}% OFF",
                            Modifier
                                .padding(4.dp)
                                .background(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DefaultColor
                                )
                                .padding(8.dp, 4.dp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }
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
