package com.starry.greenstash.database

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

    suspend fun  updateCurrentAmount(id: Int, amount: Float) {
        itemDao.updateCurrentAmount(id, amount)
    }

    suspend fun updateDeadline(id: Int, deadline: String) {
        itemDao.updateDeadline(id, deadline)
    }

    suspend fun updateItemImage(id: Int, itemImage: String) {
        itemDao.updateItemImage(id, itemImage)
    }
}