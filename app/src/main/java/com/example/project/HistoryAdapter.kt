package com.example.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val historyItems: List<HistoryItem>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = historyItems[position]
        holder.tableNameTextView.text = currentItem.tableName
        holder.bookingTimeTextView.text = "Booking Time: ${currentItem.bookingTime}"
        holder.bookerNameTextView.text = "Booker Name: ${currentItem.bookerName}"
        holder.currentTimeTextView.text = "Current Time: ${currentItem.currentTime}"
    }

    override fun getItemCount() = historyItems.size

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tableNameTextView: TextView = itemView.findViewById(R.id.tableNameTextView)
        val bookingTimeTextView: TextView = itemView.findViewById(R.id.bookingTimeTextView)
        val bookerNameTextView: TextView = itemView.findViewById(R.id.bookerNameTextView)
        val currentTimeTextView: TextView = itemView.findViewById(R.id.currentTimeTextView)
    }
}
