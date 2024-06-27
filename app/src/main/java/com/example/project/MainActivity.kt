package com.example.project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var formTitle: TextView
    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var messageTextView: TextView
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var createAccountButton: Button
    private lateinit var backButton: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        formTitle = findViewById(R.id.formTitle)
        emailEditText = findViewById(R.id.emailEditText)
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        messageTextView = findViewById(R.id.messageTextView)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        createAccountButton = findViewById(R.id.createAccountButton)
        backButton = findViewById(R.id.backButton)
        dbHelper = DatabaseHelper(this)

        loginButton.setOnClickListener { loginUser() }
        registerButton.setOnClickListener { showRegistrationFields() }
        createAccountButton.setOnClickListener { createAccount() }
        backButton.setOnClickListener { goBackToLogin() }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            messageTextView.text = "Please fill in all fields"
            return
        }

        val userExists = dbHelper.getUser(email, password)
        if (userExists) {
            messageTextView.text = "Login successful"

            // Create an Intent to navigate to SecondActivity
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent) // Start SecondActivity
        } else {
            messageTextView.text = "Incorrect email or password"
        }
    }

    private fun showRegistrationFields() {
        usernameEditText.visibility = View.VISIBLE
        confirmPasswordEditText.visibility = View.VISIBLE
        createAccountButton.visibility = View.VISIBLE
        backButton.visibility = View.VISIBLE
        registerButton.visibility = View.GONE
        loginButton.visibility = View.GONE
        formTitle.text = "Register"
    }

    private fun createAccount() {
        val email = emailEditText.text.toString()
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageTextView.text = "Please fill in all fields"
            return
        }

        if (password != confirmPassword) {
            messageTextView.text = "Passwords do not match"
            return
        }

        val userExists = dbHelper.userExists(email)
        if (userExists) {
            messageTextView.text = "User already exists"
            return
        }

        val success = dbHelper.addUser(email, username, password)
        if (success) {
            messageTextView.text = "Account created successfully"
            clearFieldsAndResetUI()
        } else {
            messageTextView.text = "Failed to create account"
        }
    }

    private fun clearFieldsAndResetUI() {
        emailEditText.text.clear()
        usernameEditText.text.clear()
        passwordEditText.text.clear()
        confirmPasswordEditText.text.clear()
        usernameEditText.visibility = View.GONE
        confirmPasswordEditText.visibility = View.GONE
        createAccountButton.visibility = View.GONE
        backButton.visibility = View.GONE
        registerButton.visibility = View.VISIBLE
        loginButton.visibility = View.VISIBLE
        messageTextView.text = ""
        formTitle.text = "Login"
    }

    private fun goBackToLogin() {
        clearFieldsAndResetUI()
    }
}
