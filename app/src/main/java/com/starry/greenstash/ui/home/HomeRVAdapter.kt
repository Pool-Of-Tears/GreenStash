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

package com.starry.greenstash.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.starry.greenstash.R
import com.starry.greenstash.database.Item
import com.starry.greenstash.utils.AppConstants
import com.starry.greenstash.utils.roundFloat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class HomeRVAdapter(private val context: Context, private val listener: ClickListenerIF) :
    RecyclerView.Adapter<HomeRVAdapter.HomeRecycleViewHolder>() {

    private val allItems = ArrayList<Item>()
    private val settingPerf = PreferenceManager.getDefaultSharedPreferences(context)

    inner class HomeRecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
        val progressBar: ProgressBar = itemView.findViewById(R.id.goalProgressBar)
        val title: TextView = itemView.findViewById(R.id.itemTitle)
        val secondaryText: TextView = itemView.findViewById(R.id.itemSecondaryText)
        val description: TextView = itemView.findViewById(R.id.itemDescriptionText)
        val depositButton: MaterialButton = itemView.findViewById(R.id.depositButton)
        val withdrawButton: MaterialButton = itemView.findViewById(R.id.withdrawButton)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecycleViewHolder {
        val viewHolder = HomeRecycleViewHolder(
            LayoutInflater.from(context).inflate(R.layout.items_cardview, parent, false)
        )
        viewHolder.depositButton.setOnClickListener {
            listener.onDepositClicked(allItems[viewHolder.adapterPosition])
        }
        viewHolder.withdrawButton.setOnClickListener {
            listener.onWithdrawClicked(allItems[viewHolder.adapterPosition])
        }
        viewHolder.editButton.setOnClickListener {
            listener.onEditClicked(allItems[viewHolder.adapterPosition])
        }
        viewHolder.deleteButton.setOnClickListener {
            listener.onDeleteClicked(allItems[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: HomeRecycleViewHolder, position: Int) {
        val currentItem = allItems[position]
        val progressPercent = ((currentItem.currentAmount / currentItem.totalAmount) * 100).toInt()
        // set goal image.
        if (currentItem.itemImage != null) {
            holder.itemImage.setImageBitmap(currentItem.itemImage)
        }
        // set goal progress.
        holder.progressBar.setProgress(progressPercent, true)
        // set goal title.
        holder.title.text = currentItem.title
        // set goal secondary text
        holder.secondaryText.text = buildGreetingText(progressPercent)
        // set goal description text.
        holder.description.text = buildDescriptionText(currentItem)
    }

    override fun getItemCount(): Int {
        return allItems.size
    }

    // Secondary text message
    private fun buildGreetingText(progressPercent: Int): String {
        val text: String = when {
            progressPercent <= 25 -> {
                context.getString(R.string.progress_greet1)
            }
            progressPercent in 26..50 -> {
                context.getString(R.string.progress_greet2)
            }
            progressPercent in 51..75 -> {
                context.getString(R.string.progress_greet3)
            }
            progressPercent in 76..99 -> {
                context.getString(R.string.progress_greet4)
            }
            else -> {
                context.getString(R.string.progress_greet5)
            }
        }
        return text
    }

    // supporting text message
    private fun buildDescriptionText(item: Item): String {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT)
        val startDate = LocalDateTime.now().format(dateFormatter)
        // calculate remaining days between today and endDate (deadline).
        val startDateValue: LocalDate = LocalDate.parse(startDate, dateFormatter)
        val endDateValue: LocalDate = LocalDate.parse(item.deadline, dateFormatter)
        val days: Long = ChronoUnit.DAYS.between(startDateValue, endDateValue)
        val remainingAmount = (item.totalAmount - item.currentAmount)
        val defCurrency = settingPerf.getString("currency", "")
        // build description string.
        if (remainingAmount.toInt() != 0) {
            var text = "You have until ${item.deadline} ($days) days left."
            if (days > 2) {
                text += "\nYou need to save around $defCurrency${roundFloat(remainingAmount / days)}/day."
                if (days > 14) {
                    val weeks = days / 7
                    text = text.dropLast(1) // remove full stop
                    text += ", $defCurrency${roundFloat(remainingAmount / weeks)}/week."
                    if (days > 60) {
                        val months = days / 30
                        text = text.dropLast(1) // remove full stop
                        text += ", $defCurrency${roundFloat(remainingAmount / months)}/month."
                    }
                }
            }
            return text
        } else {
            return context.getString(R.string.goal_achived_desc)
        }


    }

    fun updateItemsList(newList: List<Item>) {
        allItems.clear()
        allItems.addAll(newList)
        notifyDataSetChanged()
    }
}