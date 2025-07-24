package br.com.qtota.ui.screen.store_detail

import com.google.android.gms.maps.model.LatLng

data class StoreDetail(
    val name: String,
    val urlImage: String?,
    val position: LatLng,
    val distance: Int,
    val address: String,
    val products: List<StoreDetailProduct>,
    val branchList: List<StoreDetailBranch>
)
