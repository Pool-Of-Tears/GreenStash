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
import com.starry.greenstash.database.ItemRepository
import com.starry.greenstash.databinding.FragmentInputBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InputViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ItemRepository

    init {
        val dao = ItemDatabase.getDatabase(application).getItemDao()
        repository = ItemRepository(dao)
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