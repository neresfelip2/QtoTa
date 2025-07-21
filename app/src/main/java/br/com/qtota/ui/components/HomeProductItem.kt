package br.com.qtota.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.data.remote.store.StoreResponse
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.theme.ProductTitle
import br.com.qtota.ui.theme.defaultPadding
import br.com.qtota.utils.StringUtils.toMonetaryString
import coil.compose.AsyncImage
import java.time.LocalDate

@Composable
internal fun HomeProductItem(product: ProductResponse, navController: NavHostController, modifier: Modifier = Modifier) {

    Card(
        modifier
            .clickable {
                navController.navigate(
                    AppRoute.ProductDetails.productId(product.id)
                )
            },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        if (product.urlImage.isNullOrBlank()) {
            Icon(
                painterResource(R.drawable.outline_photo_24), null,
                Modifier
                    .height(128.dp)
                    .fillMaxWidth(),
                Color.LightGray
            )
        } else {
            AsyncImage(product.urlImage, null, Modifier
                .height(128.dp)
                .fillMaxWidth())
        }
        Column(Modifier
            .padding(defaultPadding)
            .fillMaxHeight()
        ) {
            ProductTitle(product.name, maxLines = 2)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    product.price.toMonetaryString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF007700)
                )
            }
            Text(
                "${product.percentageOfAverage}% abaixo da média no ${product.store.name}",
                fontSize = 12.sp,
                lineHeight = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

    }

}

@Composable @Preview(showBackground = true)
private fun HomeProductItemPreview() {
    HomeProductItem(
        product = ProductResponse(
            id = 0,
            name = "Teste",
            expirationOffer = LocalDate.now(),
            price = 10.0,
            percentageOfAverage = 10,
            urlImage = null,
            store = StoreResponse(
                id = 0,
                name = "Teste",
                branch = "Teste",
                distance = 600,
                logo = null
            )
        ),
        navController = rememberNavController()
    )
}