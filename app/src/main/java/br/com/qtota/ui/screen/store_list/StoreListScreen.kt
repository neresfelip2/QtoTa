package br.com.qtota.ui.screen.store_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import br.com.qtota.R
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.components.StoreListItem
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.defaultPadding

@Composable
internal fun StoreListScreen(bottomNavController: NavHostController) {

    val viewModel: StoreListViewModel = hiltViewModel()

    val storeListState by viewModel.storeListState.collectAsState()

    when(storeListState) {
        is UIState.Loading -> LoadingComponent(Modifier.fillMaxSize())
        is UIState.Error -> ErrorComponent(stringResource(R.string.error_loading_message), Modifier.fillMaxSize())
        is UIState.Success -> {
            val stores = (storeListState as UIState.Success).data
            if(stores.isEmpty()) {
                MessageContent(
                    {
                        Icon(
                            painterResource(R.drawable.outline_store_24),
                            null,
                            Modifier.size(128.dp),
                            tint = Color(0x59187270)
                        )
                    },
                    stringResource(R.string.any_store_found),
                    modifier = Modifier
                        .fillMaxSize(),
                )
                return
            }

            LazyVerticalGrid(GridCells.Fixed(2), contentPadding = PaddingValues(8.dp)) {

                item(span = { GridItemSpan(2) } ) {
                    Box(Modifier.fillMaxWidth()) {
                        TextButton(
                            {
                                bottomNavController.navigate(AppRoute.Map.createRoute())
                            },
                            Modifier
                                .padding(end = defaultPadding)
                                .align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                painterResource(R.drawable.outline_map_24),
                                null,
                                tint = DefaultColor
                            )
                            Spacer(Modifier.width(defaultPadding))
                            Text(stringResource(R.string.see_maps), color = DefaultColor)
                        }
                    }
                }

                items(stores) { store ->
                    StoreListItem(store, bottomNavController)
                }
            }
        }

    }

}