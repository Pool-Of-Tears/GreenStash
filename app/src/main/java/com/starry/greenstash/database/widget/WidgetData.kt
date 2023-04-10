package com.starry.greenstash.database.widget

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widget_data")
data class WidgetData(
    @PrimaryKey val appWidgetId: Int,
    val goalId: Long
)