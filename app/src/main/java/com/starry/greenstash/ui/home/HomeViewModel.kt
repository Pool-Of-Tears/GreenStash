package com.starry.greenstash.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.Item
import com.starry.greenstash.database.ItemDatabase
import com.starry.greenstash.database.ItemsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application){
    private val repository: ItemsRepository
    val allItems: LiveData<List<Item>>

    init {
        val dao = ItemDatabase.getDatabase(application).getItemDao()
        repository = ItemsRepository(dao)
        allItems = repository.allItems
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(item)
        }
    }

    fun updateCurrentAmount(id: Int, amount: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCurrentAmount(id, amount)
        }
    }

}