package com.starry.greenstash.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "greenstash")
data class Item(
    val title: String,
    val amount: Float,
    val itemImg: Bitmap,
    val deadline: Date?
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}