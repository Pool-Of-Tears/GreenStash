package com.starry.greenstash.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saving_goal")
data class Goal(
    @PrimaryKey(autoGenerate = true) val goalId: Long,
    val title: String,
    val targetAmount: Double,
    val deadline: String,
    val goalImage: Bitmap?,
    val additionalNotes: String,
)