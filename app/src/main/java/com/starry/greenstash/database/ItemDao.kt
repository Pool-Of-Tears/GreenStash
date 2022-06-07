package com.starry.greenstash.database

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemDao {

    @Query("SELECT * FROM greenstash ORDER BY id ASC")
    fun getAllItems(): LiveData<List<Item>>

    @Query("SELECT * FROM greenstash WHERE id = :id")
    suspend fun getItem(id: Int): Item

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("UPDATE greenstash SET totalAmount = :amount WHERE id = :id" )
    suspend fun  updateTotalAmount(id: Int, amount: Float)

    @Query("UPDATE greenstash SET currentAmount = :amount WHERE id = :id" )
    suspend fun  updateCurrentAmount(id: Int, amount: Float)

    @Query("UPDATE greenstash SET title = :title, totalAmount = :totalAmount," +
            "currentAmount = :currentAmount, itemImage = :itemImage," +
            "deadline = :deadline WHERE id LIKE :id")
    suspend fun updateItemm(id: Int, title: String, totalAmount: Float, currentAmount: Float,
                    itemImage: Bitmap, deadline: String
    )

}