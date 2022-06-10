package com.starry.greenstash.database

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

    @Query("UPDATE greenstash SET deadline = :deadline WHERE id = :id" )
    suspend fun updateDeadline(id: Int, deadline: String)

    @Query("UPDATE greenstash SET itemImage= :itemImage WHERE id = :id" )
    suspend fun updateItemImage(id: Int, itemImage: String)

}