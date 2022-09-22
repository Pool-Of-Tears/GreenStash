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
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.starry.greenstash.R
import com.starry.greenstash.database.Item
import com.starry.greenstash.database.ItemDao
import com.starry.greenstash.database.Transaction
import com.starry.greenstash.utils.AppConstants
import com.starry.greenstash.utils.roundFloat
import com.starry.greenstash.utils.toToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val itemDao: ItemDao,
    private val context: Context
) : ViewModel() {

    var allItems: LiveData<List<Item>> = getSortedItems()

    fun deleteItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            itemDao.delete(item)
        }
    }

    fun deposit(newAmount: Float, item: Item, context: Context) {
        val newAmountRounded = roundFloat(newAmount)
        val newCurrentAmount = item.currentAmount + newAmountRounded
        viewModelScope.launch(Dispatchers.IO) {
            itemDao.updateCurrentAmount(item.id, newCurrentAmount)
            addTransaction(item, newAmountRounded, AppConstants.TRANSACTION_DEPOSIT)
        }
        context.getString(R.string.deposit_successful).toToast(context)
    }

    fun withdraw(newAmount: Float, item: Item, context: Context) {
        if (newAmount > item.currentAmount) {
            context.getString(R.string.withdraw_overflow_error).toToast(context)
        } else {
            val newAmountRounded = roundFloat(newAmount)
            val newCurrentAmount = item.currentAmount - newAmountRounded
            viewModelScope.launch(Dispatchers.IO) {
                itemDao.updateCurrentAmount(item.id, newCurrentAmount)
                addTransaction(item, newAmountRounded, AppConstants.TRANSACTION_WITHDRAW)
            }
            context.getString(R.string.withdraw_successful).toToast(context)
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

    private fun getSortedItems(): LiveData<List<Item>> {
        val settingPerf = PreferenceManager.getDefaultSharedPreferences(context)
        // get sorted array according to preference
        return when(settingPerf.getString("sorting_order", "alphabetical_AtoZ")) {
            "alphabetical_AtoZ" -> itemDao.getItemsByAlphabeticalAsc()
            "alphabetical_ZtoA" -> itemDao.getItemsByAlphabeticalDesc()
            "goalAmount_ascending" -> itemDao.getItemsByAmountAsc()
            "goalAmount_descending" -> itemDao.getItemsByAmountDesc()
            "amountSaved_ascending" -> itemDao.getItemsByAmountSavedAsc()
            "amountSaved_descending" -> itemDao.getItemsByAmountSavedDesc()
            "dueDate_ascending" -> itemDao.getItemsByDueDateAsc()
            "dueDate_descending" -> itemDao.getItemsByDueDateDesc()
            else -> itemDao.getAllItems()
        }
    }


}