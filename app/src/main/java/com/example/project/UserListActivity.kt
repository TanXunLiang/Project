package com.example.project

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.DatabaseHelper
import com.google.android.material.navigation.NavigationView

class UserListActivity : AppCompatActivity(), UserAdapter.UserClickListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val users = mutableListOf<User>()
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize the DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)

        // Set up the navigation button in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.sidebar)

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Load users from the database
        users.addAll(dbHelper.getAllUsers())
        userAdapter = UserAdapter(users, this, dbHelper)
        recyclerView.adapter = userAdapter
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

    override fun onUserClick(position: Int) {
        // Handle user item click
        val user = users[position]
        Toast.makeText(this, "Clicked on: ${user.username}", Toast.LENGTH_SHORT).show()
    }
}
