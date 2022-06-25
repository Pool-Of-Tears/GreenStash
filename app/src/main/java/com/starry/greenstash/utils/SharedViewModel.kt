/*
MIT License

Copyright (c) 2022 Stɑrry Shivɑm

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
package com.starry.greenstash.utils

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import com.starry.greenstash.database.Item

data class ItemEditData(
    val id: Int,
    val title: String,
    val amount: String,
    val date: String,
    val image: Bitmap?
)

/*
A [SharedViewModel] class used to share data across different fragments and activities.
Note that this class must always be owned by [MainActivity] to work properly.
 */
class SharedViewModel(application: Application) : AndroidViewModel(application) {
    /*
    storing app lock status to avoid asking for authentication
    when activity restarts like when changing app or device
    theme or when changing device orientation.
    */
    var appUnlocked = false

    // Used to share item from home fragment to info fragment.
    private var infoItemData: Item? = null

    // used to share item's data to input fragment when user clicks on edit button.
    private var itemEditData: ItemEditData? = null

    fun setInfoItem(item: Item) {
        infoItemData = item
    }

    fun getInfoItem(): Item {
        return infoItemData!!
    }

    fun getEditData(): ItemEditData? {
        return if (itemEditData != null) {
            val toReturn = itemEditData
            itemEditData = null
            toReturn
        } else {
            null
        }
    }

    fun setEditData(editData: ItemEditData) {
        itemEditData = editData
    }


}