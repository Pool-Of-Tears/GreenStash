package com.starry.greenstash.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saving_goal")
data class Goal(
    val title: String,
    val targetAmount: Double,
    val deadline: String,
    val goalImage: Bitmap?,
    val additionalNotes: String,
) {
    @PrimaryKey(autoGenerate = true)
    var goalId: Long = 0L
}