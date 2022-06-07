package com.starry.greenstash.ui.input

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.starry.greenstash.database.ItemDatabase
import com.starry.greenstash.database.ItemsRepository
import com.starry.greenstash.databinding.FragmentInputBinding

class inputViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ItemsRepository

    init {
        val dao = ItemDatabase.getDatabase(application).getItemDao()
        repository = ItemsRepository(dao)
    }

    fun insertItem(binding: FragmentInputBinding) {

    }
}