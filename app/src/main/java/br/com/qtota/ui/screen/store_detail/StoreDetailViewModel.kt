package br.com.qtota.ui.screen.store_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import br.com.qtota.ui.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoreDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id: Long = savedStateHandle[AppRoute.StoreDetail.ARG_ID]!!

}