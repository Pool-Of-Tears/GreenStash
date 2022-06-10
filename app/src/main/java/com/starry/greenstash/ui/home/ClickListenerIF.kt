package com.starry.greenstash.ui.home

import com.starry.greenstash.database.Item

interface ClickListenerIF {
    fun onDepositClicked(item: Item)

    fun onWithdrawClicked(item: Item)

    fun onEditClicked(item: Item)

    fun onDeleteClicked(item: Item)
}