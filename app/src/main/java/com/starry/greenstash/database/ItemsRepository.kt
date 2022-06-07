package com.starry.greenstash.database

import android.graphics.Bitmap
import androidx.lifecycle.LiveData

class ItemsRepository(private val itemDao: ItemDao) {
    val allItems: LiveData<List<Item>> = itemDao.getAllItems()

    suspend fun getItem(id: Int) {
        itemDao.getItem(id)
    }

    suspend fun insertItem(item: Item) {
        itemDao.insert(item)
    }

    suspend fun deleteItem(item: Item) {
        itemDao.delete(item)
    }

    suspend fun  updateTotalAmount(id: Int, amount: Float) {
        itemDao.updateTotalAmount(id, amount)
    }

    suspend fun  updateCurrrentAmount(id: Int, amount: Float) {
        itemDao.updateCurrentAmount(id, amount)
    }

    suspend fun updateItemm(id: Int, title: String, totalAmount: Float, currentAmount: Float,
                            itemImage: Bitmap, deadline: String
    ) {
        itemDao.updateItemm(id, title, totalAmount, currentAmount, itemImage, deadline)
    }
}