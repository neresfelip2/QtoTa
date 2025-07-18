package br.com.qtota.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import br.com.qtota.R
import br.com.qtota.data.remote.store.StoreResponse
import br.com.qtota.ui.theme.defaultPadding
import br.com.qtota.utils.StringUtils.toDistanceString
import coil.compose.AsyncImage

@Composable
internal fun StoreListItem(store: StoreResponse, onClick: () -> Unit) {
    Card(
        onClick,
        Modifier.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

    ) {
        Column(
            Modifier.padding(defaultPadding).fillMaxWidth(),
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
            Text(store.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 2, textAlign = TextAlign.Center, lineHeight = 14.sp)
            Text(store.distance.toDistanceString(), fontSize = 12.sp, color = Color.Gray)
        }
    }
}