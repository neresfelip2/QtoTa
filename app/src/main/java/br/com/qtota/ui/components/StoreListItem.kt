package br.com.qtota.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.qtota.R
import br.com.qtota.data.remote.store.StoreResponse
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.theme.defaultPadding
import br.com.qtota.utils.StringUtils.toDistanceString
import coil.compose.AsyncImage

@Composable
internal fun StoreListItem(store: StoreResponse, navController: NavController) {
    Card(
        {
            navController.navigate(AppRoute.SearchProduct.createRoute(store = store))
        },
        Modifier.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

    ) {
        Box {
            Column(
                Modifier
                    .padding(defaultPadding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                store.logo?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        contentScale = ContentScale.Inside,
                        modifier = Modifier.size(96.dp),
                    )
                } ?: Icon(
                    painterResource(R.drawable.outline_store_24), null,
                    Modifier.size(96.dp),
                    tint = Color.LightGray
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    store.name,
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, null, Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(store.distance.toDistanceString(), fontSize = 12.sp, color = Color.Gray)
                }
            }

            IconButton({},
                Modifier
                    .padding(defaultPadding)
                    .size(20.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(Icons.Outlined.FavoriteBorder, null)
            }

        }
    }
}