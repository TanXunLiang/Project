package com.example.project

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SecondActivity : AppCompatActivity(), TableAdapter.TableClickListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var tableAdapter: TableAdapter
    private lateinit var UserButton: Button
    private lateinit var LogoutButton: Button
    private lateinit var HistoryButton: Button
    private val tableItems = mutableListOf<TableItem>()
    private var selectedTablePosition: Int = RecyclerView.NO_POSITION

    // Initialize DatabaseHelper
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize the DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)

        // Set up the navigation button in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.sidebar) // Use an appropriate icon
        UserButton = findViewById(R.id.User)
        LogoutButton = findViewById(R.id.Logout)
        HistoryButton = findViewById(R.id.History)

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        tableAdapter = TableAdapter(tableItems, this, dbHelper)
        recyclerView.adapter = tableAdapter

        // Populate initial tableItems (e.g., with dummy data or database fetch)
        populateInitialTableItems()

        // Set click listeners for buttons
        val addButton: Button = findViewById(R.id.addButton)
        val removeButton: Button = findViewById(R.id.removeButton)
        val bookButton: Button = findViewById(R.id.bookButton)
        val checkInButton: Button = findViewById(R.id.checkInButton)
        val freeButton: Button = findViewById(R.id.freeButton)

        addButton.setOnClickListener {
            addNewTable()
        }

        removeButton.setOnClickListener {
            removeLastTable()
        }

        bookButton.setOnClickListener {
            updateTableStatus("booked")
        }

        checkInButton.setOnClickListener {
            updateTableStatus("occupied")
        }

        freeButton.setOnClickListener {
            updateTableStatus("available")
        }

        UserButton.setOnClickListener {
            launchUserActivity()
        }

        LogoutButton.setOnClickListener {
            launchMainActivity()
        }
        HistoryButton.setOnClickListener {
            launchHistoryActivity()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun launchUserActivity() {
        val intent = Intent(this, UserListActivity::class.java)
        startActivity(intent)
    }

    private fun launchMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    private fun launchHistoryActivity() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }
    private fun populateInitialTableItems() {
        // Populate tableItems with initial data from the database
        tableItems.addAll(dbHelper.getAllTables())
        tableAdapter.notifyDataSetChanged()
    }

    private fun addNewTable() {
        val newTableNumber = tableItems.size + 1
        val newTable = TableItem(0, newTableNumber, "available", 0L)
        val added = dbHelper.addTable(newTableNumber, "available", 0L, "")
        if (added) {
            tableItems.add(newTable)
            tableAdapter.notifyItemInserted(tableItems.size - 1)
        }
    }

    private fun removeLastTable() {
        if (tableItems.isNotEmpty()) {
            val lastTable = tableItems.last()
            val removed = dbHelper.deleteTable(lastTable.id)
            if (removed) {
                tableItems.removeAt(tableItems.size - 1)
                tableAdapter.notifyItemRemoved(tableItems.size)
            }
        }
    }

    private fun updateTableStatus(status: String) {
        if (selectedTablePosition != RecyclerView.NO_POSITION) {
            val tableItem = tableItems[selectedTablePosition]
            val currentTime = System.currentTimeMillis()
            val formattedCurrentTime = convertToAmPm(currentTime)

            if (status == "available") {
                val formattedBookingTime = convertToAmPm(tableItem.bookingTime)
                dbHelper.addHistoryRecord(
                    "Table ${tableItem.tableNumber}",
                    formattedBookingTime,
                    tableItem.bookerName,
                    formattedCurrentTime
                )
            }
            tableItem.status = status
            tableAdapter.notifyItemChanged(selectedTablePosition)
            dbHelper.updateTable(tableItem.id, status, tableItem.bookingTime, tableItem.bookerName)
        } else {
            Toast.makeText(this, "Please select a table first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertToAmPm(time: Long): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(Date(time))
    }

    override fun onTableClick(position: Int) {
        selectedTablePosition = position
        tableAdapter.notifyDataSetChanged() // Notify adapter to refresh the item views
    }
}