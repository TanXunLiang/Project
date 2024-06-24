package com.example.project

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.DatabaseHelper

class TableAdapter(
    private val tableItems: List<TableItem>,
    private val listener: TableClickListener,
    private val dbHelper: DatabaseHelper // Pass DatabaseHelper instance
) : RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    interface TableClickListener {
        fun onTableClick(position: Int)
    }

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val tableNumberTextView: TextView = itemView.findViewById(R.id.tableNumberTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        val bookingTimeSpinner: Spinner = itemView.findViewById(R.id.bookingTimeSpinner)
        val bookerNameEditText: EditText = itemView.findViewById(R.id.bookerNameEditText)

        init {
            itemView.setOnClickListener(this)
            bookingTimeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedTime = parent.getItemAtPosition(position) as String
                    tableItems[adapterPosition].bookingTime = parseTime(selectedTime)
                    updateTableInDatabase(adapterPosition)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            bookerNameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    tableItems[adapterPosition].bookerName = s.toString()
                    updateTableInDatabase(adapterPosition)
                }
            })
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                notifyItemChanged(selectedPosition)
                selectedPosition = position
                notifyItemChanged(selectedPosition)
                listener.onTableClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_table, parent, false)
        return TableViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val currentItem = tableItems[position]
        holder.tableNumberTextView.text = "Table ${currentItem.tableNumber}"
        holder.statusTextView.text = currentItem.status
        holder.bookerNameEditText.setText(currentItem.bookerName)

        val timeSlots = generateTimeSlots()
        val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, timeSlots)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.bookingTimeSpinner.adapter = adapter

        val selectedPosition = timeSlots.indexOf(formatTime(currentItem.bookingTime))
        if (selectedPosition != -1) {
            holder.bookingTimeSpinner.setSelection(selectedPosition)
        }

        if (position == this.selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.selected_table_background)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.default_table_background)
        }
    }

    override fun getItemCount(): Int {
        return tableItems.size
    }

    private fun generateTimeSlots(): List<String> {
        val timeSlots = mutableListOf<String>()
        var hour = 0
        var minute = 0
        while (hour < 24) {
            val period = if (hour < 12) "AM" else "PM"
            val displayHour = if (hour % 12 == 0) 12 else hour % 12
            val formattedTime = String.format("%02d:%02d %s", displayHour, minute, period)
            timeSlots.add(formattedTime)
            minute += 15
            if (minute == 60) {
                minute = 0
                hour += 1
            }
        }
        return timeSlots
    }

    private fun parseTime(time: String): Long {
        val parts = time.split(" ", ":")
        val hour = parts[0].toInt() % 12 + if (parts[2] == "PM") 12 else 0
        val minute = parts[1].toInt()
        return hour * 60L * 60L * 1000L + minute * 60L * 1000L
    }

    private fun formatTime(timeInMillis: Long): String {
        val totalMinutes = timeInMillis / (60 * 1000)
        val hour = (totalMinutes / 60).toInt()
        val minute = (totalMinutes % 60).toInt()
        val period = if (hour < 12) "AM" else "PM"
        val displayHour = if (hour % 12 == 0) 12 else hour % 12
        return String.format("%02d:%02d %s", displayHour, minute, period)
    }

    private fun updateTableInDatabase(position: Int) {
        val tableItem = tableItems[position]
        dbHelper.updateTable(tableItem.id, tableItem.status, tableItem.bookingTime, tableItem.bookerName)
    }
}
