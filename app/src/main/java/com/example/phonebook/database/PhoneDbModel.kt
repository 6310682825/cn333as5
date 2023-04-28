package com.example.phonebook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhoneDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "tag") val tag: String,
    @ColumnInfo(name = "color_id") val colorId: Long,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean
) {
    companion object {
        val DEFAULT_PHONE_BOOK = listOf(
            PhoneDbModel(1, "Big Bro", "8842351113", "Family", 2, false),
            PhoneDbModel(2, "Kobra Office", "9942305436", "Office", 4, false),
            PhoneDbModel(3, "Dad", "886430008", "Family", 2, false),
            PhoneDbModel(4, "Kursh Delivery", "991557433", "Other", 5, false),
            PhoneDbModel(5, "Adam", "886350599", "Mobile", 1, false),
            PhoneDbModel(6, "Jane", "882415513", "Mobile", 1, false),
            PhoneDbModel(7, "Auntie's home", "991630331", "Home", 3, false)
        )
    }
}
