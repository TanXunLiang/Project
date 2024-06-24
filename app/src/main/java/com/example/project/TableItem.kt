package com.example.project

import android.os.Parcel
import android.os.Parcelable

data class TableItem(
    val id: Int,
    val tableNumber: Int,
    var status: String,
    var bookingTime: Long,
    var bookerName: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(tableNumber)
        parcel.writeString(status)
        parcel.writeLong(bookingTime)
        parcel.writeString(bookerName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TableItem> {
        override fun createFromParcel(parcel: Parcel): TableItem {
            return TableItem(parcel)
        }

        override fun newArray(size: Int): Array<TableItem?> {
            return arrayOfNulls(size)
        }
    }
}
