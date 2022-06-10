package com.starry.greenstash.ui.home

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.starry.greenstash.R
import com.starry.greenstash.database.Item
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class HomeRVAdapter(private val context: Context, private val listener: ClickListenerIF) :
    RecyclerView.Adapter<HomeRVAdapter.HomeRecycleViewHolder>() {

    private val allItems = ArrayList<Item>()

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
            holder.itemImage.setImageURI(Uri.parse(currentItem.itemImage))
        }
        // set goal progress.
        holder.progressBar.setProgress(progressPercent, true)
        // set goal title.
        holder.title.setText(currentItem.title)
        // set goal secondary text
        holder.secondaryText.setText(buildGreetingText(progressPercent))

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
    fun buildDescriptionText(item: Item) {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/u")
        val startDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        // calculate remaining days between today and endDate (deadline).
        val startDateValue: LocalDate = LocalDate.parse(startDate, dateFormatter)
        val endDateValue: LocalDate = LocalDate.parse(item.deadline, dateFormatter)
        val days: Long = ChronoUnit.DAYS.between(startDateValue, endDateValue)
        // build description string.
        val remainingAmount = item.totalAmount - item.currentAmount
        var text: String = "You have until ${item.deadline} ($days) days left."
        // TODO: finish this func.
        if (days > 2) {
            text += "\nYou need to save around ${remainingAmount / days}/day."
        }
    }

    fun updateItemsList(newList: List<Item>) {
        allItems.clear()
        allItems.addAll(newList)
        notifyDataSetChanged()
    }
}