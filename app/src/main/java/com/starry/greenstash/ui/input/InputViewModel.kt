package com.starry.greenstash.ui.input

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.cutetoast.CuteToast
import com.starry.greenstash.R
import com.starry.greenstash.database.Item
import com.starry.greenstash.database.ItemDatabase
import com.starry.greenstash.database.ItemsRepository
import com.starry.greenstash.databinding.FragmentInputBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InputViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ItemsRepository

    init {
        val dao = ItemDatabase.getDatabase(application).getItemDao()
        repository = ItemsRepository(dao)
    }

    fun insertItem(binding: FragmentInputBinding, imageData: Intent?, ctx: Context): Boolean {
        val title = binding.inputTitle.text
        val amount = binding.inputAmount.text
        val deadline = binding.inputDeadline.text

        if (title.isEmpty() || title.isBlank()) {
            CuteToast.ct(
                ctx, ctx.getString(R.string.title_empty_err),
                CuteToast.LENGTH_SHORT,
                CuteToast.SAD, true
            ).show()
            return false
        } else if (amount.isEmpty() || amount.isBlank()) {
            CuteToast.ct(
                ctx, ctx.getString(R.string.amount_empty_err),
                CuteToast.LENGTH_SHORT,
                CuteToast.SAD, true
            ).show()
            return false
        } else if (deadline.isEmpty() || deadline.isBlank()) {
            CuteToast.ct(
                ctx, ctx.getString(R.string.deadline_empty_err),
                CuteToast.LENGTH_SHORT,
                CuteToast.SAD, true
            ).show()
            return false
        } else {
            val item = if (imageData != null) {
                Item(
                    title.toString(),
                    totalAmount = amount.toString().toFloat(),
                    itemImage = imageData.data.toString(),
                    deadline = deadline.toString(),
                )
            } else {
                Item(
                    title.toString(),
                    totalAmount = amount.toString().toFloat(),
                    itemImage = null,
                    deadline = deadline.toString(),
                )
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.insertItem(item)
            }
            CuteToast.ct(
                ctx, ctx.getString(R.string.data_saved_success),
                CuteToast.LENGTH_SHORT,
                CuteToast.SUCCESS, true
            ).show()
            return true
        }
    }
}