package com.example.phonebook.database

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ColorDao {

    @Query("SELECT * FROM ColorDbModel")
    fun getAll(): LiveData<List<ColorDbModel>>

    @Query("SELECT * FROM ColorDbModel")
    fun getAllSync(): List<ColorDbModel>

    @Insert
    fun insertAll(vararg colorDbModel: ColorDbModel)

}