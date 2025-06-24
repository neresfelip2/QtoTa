package br.com.qtota.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "Product")
data class Product(

    @PrimaryKey
    val id: Long,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("description")
    val description: String,

    @ColumnInfo("current_value")
    val currentValue: Double,

    @ColumnInfo("previous_value")
    val previousValue: Double?,

    @ColumnInfo("store_name")
    val storeName: String,

    @ColumnInfo("distance")
    val distance: Int,

    @ColumnInfo("expiration_date")
    val expirationDate: LocalDate,

)