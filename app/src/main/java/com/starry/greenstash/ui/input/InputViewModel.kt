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

import android.content.Context
import android.graphics.Bitmap
import android.text.Editable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.cutetoast.CuteToast
import com.starry.greenstash.R
import com.starry.greenstash.database.Item
import com.starry.greenstash.database.ItemDao
import com.starry.greenstash.databinding.FragmentInputBinding
import com.starry.greenstash.utils.ItemEditData
import com.starry.greenstash.utils.roundFloat
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
        editData: ItemEditData? = null
    ): Boolean {
        val title = binding.inputTitle.editText?.text!!
        val amount = binding.inputAmount.editText?.text!!
        val deadline = binding.inputDeadline.editText?.text!!

        // validate user input.
        if (!validateInputs(ctx, title, amount, deadline)) {
            return false
            // Insert or update the item.
        } else {
            if (editData == null) {
                val item = if (imageData != null) {
                    Item(
                        title.toString(),
                        totalAmount = amount.toString().toFloat(),
                        itemImage = imageData,
                        deadline = deadline.toString(),
                        transactions = null
                    )
                } else {
                    Item(
                        title.toString(),
                        totalAmount = roundFloat(amount.toString().toFloat()),
                        itemImage = null,
                        deadline = deadline.toString(),
                        transactions = null
                    )
                }
                viewModelScope.launch(Dispatchers.IO) {
                    itemDao.insert(item)
                }
                CuteToast.ct(
                    ctx, ctx.getString(R.string.data_saved_success),
                    CuteToast.LENGTH_SHORT,
                    CuteToast.SUCCESS, true
                ).show()
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    if (imageData != null) {
                        itemDao.updateItemImage(editData.id, imageData)
                    }
                    itemDao.updateTitle(editData.id, title.toString())
                    itemDao.updateTotalAmount(
                        editData.id,
                        roundFloat(amount.toString().toFloat())
                    )
                    itemDao.updateDeadline(editData.id, deadline.toString())
                }
            }
            return true
        }
    }

    private fun validateInputs(
        ctx: Context,
        title: Editable,
        amount: Editable,
        deadline: Editable
    ): Boolean {
        // validate user input.
        if (title.isEmpty() || title.isBlank()) {
            CuteToast.ct(
                ctx, ctx.getString(R.string.title_empty_err),
                CuteToast.LENGTH_SHORT,
                CuteToast.SAD, true
            ).show()
            return false
        } else if (amount.isEmpty() || amount.isBlank() || amount.toString().toFloat() == 0f) {
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
            return true
        }
    }
}