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

}