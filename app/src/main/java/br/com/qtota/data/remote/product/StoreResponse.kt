package br.com.qtota.data.remote.product

import java.time.LocalDate

data class StoreResponse(
    val name: String,
    val distance: Int,
    val price: Double,
    val date: LocalDate
)