package com.example.phonebook.domain.model

import android.support.v4.os.IResultReceiver.Default
import com.example.phonebook.database.ColorDbModel

data class ColorModel(
    val id: Long,
    val color_code: String,
    val color: String,
    val type: String
) {

    companion object {
        val DEFAULT = with(ColorDbModel.DEFAULT_COLOR) {ColorModel(id, colorCode, color, type)}
    }
}