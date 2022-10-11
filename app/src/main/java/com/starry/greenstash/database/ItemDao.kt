/*
MIT License

Copyright (c) 2022 Stɑrry Shivɑm // This file is part of GreenStash.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.starry.greenstash.database

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemDao {

    @Query("SELECT * FROM greenstash ORDER BY id ASC")
    fun getAllItems(): LiveData<List<Item>>

    @Query("SELECT * FROM greenstash ORDER BY title ASC")
    fun getItemsByAlphabeticalAsc(): LiveData<List<Item>>

    @Query("SELECT * FROM greenstash ORDER BY title DESC")
    fun getItemsByAlphabeticalDesc(): LiveData<List<Item>>

    @Query("SELECT * FROM greenstash ORDER BY totalAmount ASC")
    fun getItemsByAmountAsc(): LiveData<List<Item>>

    @Query("SELECT * FROM greenstash ORDER BY totalAmount DESC")
    fun getItemsByAmountDesc(): LiveData<List<Item>>

    @Query("SELECT * FROM greenstash ORDER BY currentAmount ASC")
    fun getItemsByAmountSavedAsc(): LiveData<List<Item>>

    @Query("SELECT * FROM greenstash ORDER BY currentAmount DESC")
    fun getItemsByAmountSavedDesc(): LiveData<List<Item>>

    @Query(
        "SELECT * FROM greenstash ORDER BY " +
                "substr(deadline, 7, 4) || '/' || " +
                "substr(deadline, 4, 2) || '/' || " +
                "substr(deadline, 1, 2) ASC"
    )
    fun getItemsByDueDateAsc(): LiveData<List<Item>>

    @Query(
        "SELECT * FROM greenstash ORDER BY " +
                "substr(deadline, 7, 4) || '/' || " +
                "substr(deadline, 4, 2) || '/' || " +
                "substr(deadline, 1, 2) DESC"
    )
    fun getItemsByDueDateDesc(): LiveData<List<Item>>

    @Query("SELECT * FROM greenstash WHERE id = :id")
    suspend fun getItem(id: Int): Item

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("UPDATE greenstash SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Int, title: String)

    @Query("UPDATE greenstash SET totalAmount = :amount WHERE id = :id")
    suspend fun updateTotalAmount(id: Int, amount: Float)

    @Query("UPDATE greenstash SET currentAmount = :amount WHERE id = :id")
    suspend fun updateCurrentAmount(id: Int, amount: Float)

    @Query("UPDATE greenstash SET deadline = :deadline WHERE id = :id")
    suspend fun updateDeadline(id: Int, deadline: String)

    @Query("UPDATE greenstash SET additionalNotes = :additionalNotes WHERE id = :id")
    suspend fun updateAdditionalNotes(id: Int, additionalNotes: String)

    @Query("UPDATE greenstash SET itemImage = :itemImage WHERE id = :id")
    suspend fun updateItemImage(id: Int, itemImage: Bitmap)

    @Query("UPDATE greenstash SET transactions = :transactions WHERE id = :id")
    suspend fun updateTransactions(id: Int, transactions: List<Transaction>)

}