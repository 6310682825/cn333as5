package com.example.phonebook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ColorDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "color_code") val colorCode: String,
    @ColumnInfo(name = "color") val color: String
) {
    companion object {
        val DEFAULT_COLORS = listOf(
            ColorDbModel(1, "#F8ECD1`", "Soft Yellow"),
            ColorDbModel(2, "#DEB6AB", "Pale Orange"),
            ColorDbModel(3, "#FEC8D8", "Candy Pink"),
            ColorDbModel(4, "#B7D3DF", "Sky Blue"),
            ColorDbModel(5, "#C9BBCF","Medium Lilac")
        )
        val DEFAULT_COLOR = DEFAULT_COLORS[0]
    }
}
