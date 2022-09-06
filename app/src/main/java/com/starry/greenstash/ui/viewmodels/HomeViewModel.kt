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
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.starry.greenstash.R
import com.starry.greenstash.database.Item
import com.starry.greenstash.database.ItemDao
import com.starry.greenstash.database.Transaction
import com.starry.greenstash.utils.AppConstants
import com.starry.greenstash.utils.roundFloat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val itemDao: ItemDao) : ViewModel() {

    val allItems: LiveData<List<Item>> = itemDao.getAllItems()

    fun deleteItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            itemDao.delete(item)
        }
    }

    fun deposit(newAmount: Float, item: Item, context: Context, view: View) {
        val newAmountRounded = roundFloat(newAmount)
        val newCurrentAmount = item.currentAmount + newAmountRounded
        viewModelScope.launch(Dispatchers.IO) {
            itemDao.updateCurrentAmount(item.id, newCurrentAmount)
            addTransaction(item, newAmountRounded, AppConstants.TRANSACTION_DEPOSIT)
        }
        Snackbar.make(
            view, context.getString(R.string.deposit_successful),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    fun withdraw(newAmount: Float, item: Item, context: Context, view: View) {
        if (newAmount > item.currentAmount) {
            Snackbar.make(
                view,
                context.getString(R.string.withdraw_overflow_error),
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            val newAmountRounded = roundFloat(newAmount)
            val newCurrentAmount = item.currentAmount - newAmountRounded
            viewModelScope.launch(Dispatchers.IO) {
                itemDao.updateCurrentAmount(item.id, newCurrentAmount)
                addTransaction(item, newAmountRounded, AppConstants.TRANSACTION_WITHDRAW)
            }
            Snackbar.make(
                view,
                context.getString(R.string.withdraw_successful),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private suspend fun addTransaction(item: Item, amount: Float, transactionType: String) {
        val currentDate =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT))
        // build new transaction object
        val newTransaction = Transaction(transactionType, currentDate, amount)
        // insert new transaction in database
        val transactions: List<Transaction> = if (item.transactions == null) {
            listOf(newTransaction)
        } else {
            item.transactions.plus(newTransaction)
        }
        itemDao.updateTransactions(item.id, transactions)
    }


}