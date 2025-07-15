package br.com.qtota.data.local.entity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "Product")
data class Product(

    @PrimaryKey
    val id: Long,

    @ColumnInfo("id_store")
    val storeId: Long,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("description")
    val description: String,

    @ColumnInfo("current_value")
    val currentPrice: Double,

    @ColumnInfo("discount_percentage")
    val discountPercentage: Int,

    @ColumnInfo("previous_value")
    val previousPrice: Double?,

    @ColumnInfo("store_name")
    val storeName: String,

    @ColumnInfo("store_branch")
    val storeBranch: String,

    @ColumnInfo("distance")
    val distance: Int,

    @ColumnInfo("expiration_date")
    val expirationOffer: LocalDate,

    @ColumnInfo("logo")
    val logo: String?,

    ) {

    var isSaved by mutableStateOf(false)

}