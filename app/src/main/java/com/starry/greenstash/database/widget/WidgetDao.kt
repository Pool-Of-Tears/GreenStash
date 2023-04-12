package com.starry.greenstash.database.widget

import androidx.room.*

@Dao
interface WidgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidgetData(widgetData: WidgetData)

    @Delete
    suspend fun deleteWidgetData(widgetData: WidgetData)

    @Update
    suspend fun updateWidgetData(widgetData: WidgetData)

    @Query("SELECT * FROM widget_data WHERE appWidgetId = :appWidgetId")
    suspend fun getWidgetData(appWidgetId: Int): WidgetData?
}