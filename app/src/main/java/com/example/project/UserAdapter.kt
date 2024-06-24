package com.example.project

import android.content.ContentValues
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.DatabaseHelper

data class User(val id: Int, var email: String, var password: String, var username: String)

class UserAdapter(
    private val users: MutableList<User>,
    private val listener: UserClickListener,
    private val dbHelper: DatabaseHelper
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    interface UserClickListener {
        fun onUserClick(position: Int)
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val email: EditText = itemView.findViewById(R.id.editEmail)
        val password: EditText = itemView.findViewById(R.id.editPassword)
        val username: EditText = itemView.findViewById(R.id.editUsername)
        val saveButton: Button = itemView.findViewById(R.id.saveButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        init {
            itemView.setOnClickListener(this)
            saveButton.setOnClickListener {
                val user = users[adapterPosition]
                user.email = email.text.toString()
                user.password = password.text.toString()
                user.username = username.text.toString()
                dbHelper.updateUser(user)
                notifyItemChanged(adapterPosition)
            }
            deleteButton.setOnClickListener {
                val user = users[adapterPosition]
                dbHelper.deleteUser(user.id)
                users.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
        }

        override fun onClick(v: View?) {
            listener.onUserClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.email.setText(user.email)
        holder.password.setText(user.password)
        holder.username.setText(user.username)
    }

    override fun getItemCount(): Int = users.size
}
