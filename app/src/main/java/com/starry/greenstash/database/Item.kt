package com.starry.greenstash.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "greenstash")
data class Item(
    val title: String,
    val totalAmount: Float,
    val currentAmount: Float,
    val itemImage: Bitmap,
    val deadline: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}