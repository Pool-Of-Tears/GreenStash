package com.starry.greenstash.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "greenstash")
data class Item(
    val title: String,
    val totalAmount: Float,
    val currentAmount: Float = 0f,
    val itemImage: String?,
    val deadline: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}