package br.com.qtota.ui.screen.product_details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.ui.components.Toolbar
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.GradientBackground
import br.com.qtota.ui.theme.GrayColor

@Composable
internal fun ProductDetailsScreen(navController: NavHostController) {

    Scaffold(
        topBar = {
            Toolbar(
                title = null,
                backButtonEnabled = navController,
                Pair(Icons.Outlined.FavoriteBorder) {},
                Pair(Icons.Outlined.Share) {}
            )
        }
    ) { innerPadding ->
        Column(Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(R.drawable.outline_photo_24),
                null,
                Modifier.height(160.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(8.dp))
            Text("Açucar Cristal União 1Kg", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Pão de Açucar", color = Color.Gray)
            Spacer(Modifier.height(16.dp))
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
                        Text("Melhor preço:", color = Color.White)
                        Text("R$ 3,49", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text("Variação:", Modifier.align(Alignment.End), color = Color.White)
                        Text("R$ 3,49 - R$ 4,99", color = Color.White)
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
                    Text("Criar alerta de preço")
                }
            }
            Spacer(Modifier.height(16.dp))

            var selectedTab by remember { mutableStateOf(Tab.TAB_PRICES) }

            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                indicator = {},
                divider = {},
            ) {

                Tab(
                    modifier = when (selectedTab) {
                        Tab.TAB_PRICES -> Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(DefaultColor)
                        Tab.TAB_DETAILS -> Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(GrayColor)
                    },
                    selected = selectedTab == Tab.TAB_PRICES,
                    onClick = { selectedTab = Tab.TAB_PRICES },
                    text = {
                        Text(text = "Preços", color = if (selectedTab == Tab.TAB_PRICES) Color.White else DefaultColor)
                    }
                )

                Tab(
                    modifier = when (selectedTab) {
                        Tab.TAB_PRICES -> Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(GrayColor)
                        Tab.TAB_DETAILS -> Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(DefaultColor)
                    },
                    selected = selectedTab == Tab.TAB_DETAILS,
                    onClick = { selectedTab = Tab.TAB_DETAILS },
                    text = {
                        Text(text = "Detalhes", color = if (selectedTab == Tab.TAB_DETAILS) Color.White else DefaultColor)
                    }
                )

            }

            Spacer(Modifier.height(16.dp))

            when(selectedTab) {
                Tab.TAB_PRICES -> PricesContainer()
                Tab.TAB_DETAILS -> DetailsContainer()
            }

        }
    }
}

@Composable @Preview(showSystemUi = true)
private fun ProductDetailsScreenPreview() {
    ProductDetailsScreen(rememberNavController())
}

@Composable @Preview(showBackground = true)
private fun PricesContainer() {
    Column {

        repeat(8) {

            Column(
                Modifier
                    .padding(vertical = 8.dp)
                    /*.background(
                                Color(0x11FF6B6B),
                                shape = RoundedCornerShape(16.dp)
                            )*/
                    .border(
                        BorderStroke(1.dp, DefaultColor),
                        RoundedCornerShape(16.dp),
                    )
            ) {

                Text(
                    "Melhor preço",
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

                Column(Modifier.padding(start = 12.dp, bottom = 12.dp, end = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painterResource(R.drawable.outline_photo_24),
                            null,
                            Modifier.size(48.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Pão de açucar",
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            Spacer(Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Outlined.Place,
                                    null,
                                    Modifier.size(16.dp),
                                    tint = Color.Gray
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("800m", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                        Text(
                            "R$ 3,49",
                            color = Color.DarkGray,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(R.drawable.outline_nest_clock_farsight_analog_24),
                            null,
                            Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Agora", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable @Preview(showBackground = true)
private fun DetailsContainer() {
    Column(Modifier
        .background(
            Color.White,
            RoundedCornerShape(16.dp)
        )
        .padding(16.dp),
    ) {
        Text("Informações", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        DetailRow("Peso", "1Kg")
        HorizontalDivider()
        DetailRow("Tipo", "Cristal")
        HorizontalDivider()
        DetailRow("Origem", "Nacional")
        HorizontalDivider()
        DetailRow("Validade", "24 meses")
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

enum class Tab {
    TAB_PRICES, TAB_DETAILS
}