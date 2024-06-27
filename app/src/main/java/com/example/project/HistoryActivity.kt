package com.example.project

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity(){
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var UserButton: Button
    private lateinit var BookingButton: Button
    private lateinit var LogoutButton: Button

    private val historyItems = mutableListOf<HistoryItem>()

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize the DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)

        // Set up the navigation button in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.sidebar) // Use an appropriate icon

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dbHelper = DatabaseHelper(this)

        UserButton = findViewById(R.id.User)
        LogoutButton = findViewById(R.id.Logout)
        BookingButton = findViewById(R.id.Booking)

        historyAdapter = HistoryAdapter(historyItems)
        recyclerView.adapter = historyAdapter

        // Populate initial history items
        populateHistoryItems()
        UserButton.setOnClickListener {
            launchUserActivity()
        }

        LogoutButton.setOnClickListener {
            launchMainActivity()
        }

        BookingButton.setOnClickListener {
            launchSecondActivity()
    }}


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun populateHistoryItems() {
        historyItems.addAll(dbHelper.getAllHistory())
        historyAdapter.notifyDataSetChanged()
    }


    private fun launchUserActivity() {
        val intent = Intent(this, UserListActivity::class.java)
        startActivity(intent)
    }

    private fun launchMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun launchSecondActivity() {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }
}