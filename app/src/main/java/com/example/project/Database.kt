package com.example.myapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.project.TableItem
import com.example.project.HistoryItem
import com.example.project.User
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDB.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_USERNAME = "username"

        private const val TABLE_TABLES = "tables"
        private const val COLUMN_TABLE_ID = "table_id"
        private const val COLUMN_TABLE_NUMBER = "table_number"
        private const val COLUMN_STATUS = "status"
        private const val COLUMN_BOOKING_TIME = "booking_time"
        private const val COLUMN_BOOKER_NAME = "booker_name"
        private const val TABLE_HISTORY = "history"
        private const val COLUMN_HISTORY_ID = "history_id"
        private const val COLUMN_HISTORY_TABLE_NAME = "table_name"
        private const val COLUMN_HISTORY_BOOKING_TIME = "history_booking_time"
        private const val COLUMN_HISTORY_BOOKER_NAME = "history_booker_name"
        private const val COLUMN_HISTORY_CURRENT_TIME = "history_current_time"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_EMAIL TEXT UNIQUE,"
                + "$COLUMN_PASSWORD TEXT,"
                + "$COLUMN_USERNAME TEXT)")
        db.execSQL(createUsersTable)

        val createTablesTable = ("CREATE TABLE $TABLE_TABLES ("
                + "$COLUMN_TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_TABLE_NUMBER INTEGER,"
                + "$COLUMN_STATUS TEXT,"
                + "$COLUMN_BOOKING_TIME INTEGER,"
                + "$COLUMN_BOOKER_NAME TEXT)")
        db.execSQL(createTablesTable)

        val createHistoryTable = ("CREATE TABLE $TABLE_HISTORY ("
                + "$COLUMN_HISTORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_HISTORY_TABLE_NAME TEXT,"
                + "$COLUMN_HISTORY_BOOKING_TIME TEXT,"
                + "$COLUMN_HISTORY_BOOKER_NAME TEXT,"
                + "$COLUMN_HISTORY_CURRENT_TIME TEXT)")
        db.execSQL(createHistoryTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TABLES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        onCreate(db)
    }

    fun addHistoryRecord(tableName: String, bookingTime: String, bookerName: String, currentTime: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_HISTORY_TABLE_NAME, tableName)
        values.put(COLUMN_HISTORY_BOOKING_TIME, bookingTime)
        values.put(COLUMN_HISTORY_BOOKER_NAME, bookerName)
        values.put(COLUMN_HISTORY_CURRENT_TIME, currentTime)
        val result = db.insert(TABLE_HISTORY, null, values)
        db.close()
        return (result != -1L)
    }

    fun addUser(email: String, username: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)
        values.put(COLUMN_USERNAME, username)
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return (result != -1L)
    }

    fun getUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(TABLE_USERS, arrayOf(COLUMN_ID, COLUMN_EMAIL, COLUMN_PASSWORD), "$COLUMN_EMAIL=? AND $COLUMN_PASSWORD=?", arrayOf(email, password), null, null, null)
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }

    fun userExists(email: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(TABLE_USERS, arrayOf(COLUMN_ID, COLUMN_EMAIL), "$COLUMN_EMAIL=?", arrayOf(email), null, null, null)
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }

    fun addTable(tableNumber: Int, status: String, bookingTime: Long, bookerName: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TABLE_NUMBER, tableNumber)
        values.put(COLUMN_STATUS, status)
        values.put(COLUMN_BOOKING_TIME, bookingTime)
        values.put(COLUMN_BOOKER_NAME, bookerName)
        val result = db.insert(TABLE_TABLES, null, values)
        db.close()
        return (result != -1L)
    }

    fun updateTable(id: Int, status: String, bookingTime: Long, bookerName: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_STATUS, status)
        values.put(COLUMN_BOOKING_TIME, bookingTime)
        values.put(COLUMN_BOOKER_NAME, bookerName)
        val result = db.update(TABLE_TABLES, values, "$COLUMN_TABLE_ID=?", arrayOf(id.toString()))
        db.close()
        return (result != 0)
    }

    fun deleteTable(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_TABLES, "$COLUMN_TABLE_ID=?", arrayOf(id.toString()))
        db.close()
        return (result != 0)
    }

    fun getAllTables(): List<TableItem> {
        val tableList = mutableListOf<TableItem>()
        val db = this.readableDatabase
        val cursor: Cursor = db.query(TABLE_TABLES, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TABLE_ID))
                val tableNumber = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TABLE_NUMBER))
                val status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))
                val bookingTime = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_TIME))
                val bookerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKER_NAME))

                tableList.add(TableItem(id, tableNumber, status, bookingTime, bookerName))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return tableList
    }

    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor: Cursor = db.query(TABLE_USERS, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
                val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))

                userList.add(User(id, email, password, username))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return userList
    }

    fun updateUser(user: User): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EMAIL, user.email)
        values.put(COLUMN_PASSWORD, user.password)
        values.put(COLUMN_USERNAME, user.username)
        val result = db.update(TABLE_USERS, values, "$COLUMN_ID=?", arrayOf(user.id.toString()))
        db.close()
        return (result != 0)
    }

    fun deleteUser(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_USERS, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return (result != 0)
    }
    fun deleteHistoryItem(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_HISTORY, "$COLUMN_HISTORY_ID=?", arrayOf(id.toString()))
        db.close()
        return (result != 0)
    }

    fun getAllHistory(): List<HistoryItem> {
        val historyList = mutableListOf<HistoryItem>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_HISTORY", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_ID))
                val tableName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_TABLE_NAME))
                val bookingTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_BOOKING_TIME))
                val bookerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_BOOKER_NAME))
                val currentTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_CURRENT_TIME))
                val historyItem = HistoryItem(id, tableName, bookingTime, bookerName, currentTime)
                historyList.add(historyItem)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return historyList
    }

}
