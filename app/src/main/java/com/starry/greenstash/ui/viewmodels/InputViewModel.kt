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

package com.starry.greenstash.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.text.Editable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.R
import com.starry.greenstash.database.Item
import com.starry.greenstash.database.ItemDao
import com.starry.greenstash.databinding.FragmentInputBinding
import com.starry.greenstash.utils.roundFloat
import com.starry.greenstash.utils.toToast
import com.starry.greenstash.utils.validateAmount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InputViewModel @Inject constructor(private val itemDao: ItemDao) : ViewModel() {

    fun insertItem(
        binding: FragmentInputBinding,
        imageData: Bitmap?,
        ctx: Context,
        editData: Item? = null
    ): Boolean {
        val title = binding.inputTitle.editText?.text!!
        val amount = binding.inputAmount.editText?.text!!
        val deadline = binding.inputDeadline.editText?.text!!
        val additionalNotes = binding.inputAdditionalNotes.editText?.text!!

        // validate user input.
        if (!validateInputs(ctx, title, amount)) {
            return false
            // Insert or update the item.
        } else {
            val newAmount = roundFloat(amount.toString().replace(',', '.').toFloat())
            if (editData == null) {
                val item = if (imageData != null) {
                    Item(
                        title = title.toString(),
                        totalAmount = newAmount,
                        itemImage = imageData,
                        deadline = deadline.toString(),
                        additionalNotes = additionalNotes.toString(),
                        transactions = null
                    )
                } else {
                    Item(
                        title = title.toString(),
                        totalAmount = newAmount,
                        itemImage = null,
                        deadline = deadline.toString(),
                        additionalNotes = additionalNotes.toString(),
                        transactions = null
                    )
                }
                viewModelScope.launch(Dispatchers.IO) {
                    itemDao.insert(item)
                }
                ctx.getString(R.string.data_saved_success).toToast(ctx)
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    if (imageData != null) {
                        itemDao.updateItemImage(editData.id, imageData)
                    }
                    itemDao.updateTitle(editData.id, title.toString())
                    itemDao.updateTotalAmount(editData.id, newAmount)
                    itemDao.updateDeadline(editData.id, deadline.toString())
                    itemDao.updateAdditionalNotes(editData.id, additionalNotes.toString())
                }
            }
            return true
        }
    }

    private fun validateInputs(
        ctx: Context,
        title: Editable,
        amount: Editable,
    ): Boolean {
        // validate user input.
        return if (title.isEmpty() || title.isBlank()) {
            ctx.getString(R.string.title_empty_err).toToast(ctx)
            false
        } else if (!(amount.validateAmount())) {
            ctx.getString(R.string.amount_empty_err).toToast(ctx)
            false
        } else {
            true
        }
    }
}