package br.com.qtota.ui.screen.store_detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.ui.components.ImageComponent
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.theme.ProductTitle
import br.com.qtota.ui.theme.defaultPadding
import br.com.qtota.utils.StringUtils.toMonetaryString

@Composable
internal fun StoreDetailProductItem(product: StoreDetailProduct, navController: NavHostController, modifier: Modifier = Modifier) {

    Card(
        modifier
            .clickable {
                navController.navigate(
                    AppRoute.ProductDetails.productId(product.id)
                )
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {

        Column(Modifier.padding(defaultPadding)) {
            ImageComponent(product.urlImage, R.drawable.outline_photo_24, 128.dp, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(defaultPadding/2))
            ProductTitle(product.name, maxLines = 2)
            Spacer(Modifier.height(defaultPadding/2))
            Text(
                product.price.toMonetaryString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF007700)
            )
            Spacer(Modifier.height(defaultPadding/2))
            Text(
                "${product.percentageOfAverage}% abaixo da média",
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
    StoreDetailProductItem(
        product = StoreDetailProduct(
            id = 0,
            name = "Teste",
            price = 10.0,
            percentageOfAverage = 10,
            urlImage = null,
        ),
        navController = rememberNavController()
    )
}