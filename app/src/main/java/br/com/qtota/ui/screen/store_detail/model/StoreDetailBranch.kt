package br.com.qtota.ui.screen.store_detail.model

import com.google.android.gms.maps.model.LatLng

data class StoreDetailBranch(
    val description: String,
    val address: String,
    val distance: Int,
    val position: LatLng
)
