package br.com.qtota.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Product")
data class Product(

    @PrimaryKey
    val id: Long,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("path_image")
    val pathImage: String?,
)