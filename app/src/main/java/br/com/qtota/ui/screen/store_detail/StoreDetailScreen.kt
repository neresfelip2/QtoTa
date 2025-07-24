package br.com.qtota.ui.screen.store_detail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun StoreDetailScreen() {

    val viewModel: StoreDetailViewModel = hiltViewModel()

    LazyColumn {
        item {
            Row {

            }
        }
    }

}

@Composable @Preview(showBackground = true)
private fun StoreDetailScreenPreview() {
    StoreDetailScreen()
}