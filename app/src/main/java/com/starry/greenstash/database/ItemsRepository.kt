package com.starry.greenstash.database

import androidx.lifecycle.LiveData

class ItemsRepository(private val itemDao: ItemDao) {
    val allItems: LiveData<List<Item>> = itemDao.getItems()

    suspend fun insertItem(item: Item) {
        itemDao.insert(item)
    }

    suspend fun deleteItem(item: Item) {
        itemDao.delete(item)
    }

    suspend fun  updateAmount(id: Int, amount: Float) {
        itemDao.updateAmount(id, amount)
    }
}