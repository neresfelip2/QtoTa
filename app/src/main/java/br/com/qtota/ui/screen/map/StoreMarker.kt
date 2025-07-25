package br.com.qtota.ui.screen.map

import com.google.android.gms.maps.model.LatLng

data class StoreMarker(
    val id: Long,
    val name: String,
    val branch: String,
    val logo: String?,
    val position: LatLng,
)