/**
 * MIT License
 *
 * Copyright (c) [2022 - Present] Stɑrry Shivɑm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.starry.greenstash.database.widget

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface WidgetDao {

    /**
     * Insert widget data.
     * @param widgetData WidgetData to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidgetData(widgetData: WidgetData)

    /**
     * Delete widget data.
     * @param widgetData WidgetData to delete.
     */
    @Delete
    suspend fun deleteWidgetData(widgetData: WidgetData)

    /**
     * Update widget data.
     * @param widgetData WidgetData to update.
     */
    @Update
    suspend fun updateWidgetData(widgetData: WidgetData)

    /**
     * Get widget data by appWidgetId.
     * @param appWidgetId AppWidgetId to get widget data.
     * @return WidgetData.
     */
    @Query("SELECT * FROM widget_data WHERE appWidgetId = :appWidgetId")
    suspend fun getWidgetData(appWidgetId: Int): WidgetData?
}