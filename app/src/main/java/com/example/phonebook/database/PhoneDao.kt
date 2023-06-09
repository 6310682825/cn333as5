package com.example.phonebook.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhoneDao {

    @Query("SELECT * FROM PhoneDbModel")
    fun getAllSync(): List<PhoneDbModel>

    @Query("SELECT * FROM PhoneDbModel WHERE id IN (:phoneIds)")
    fun getPhoneByIdsSync(phoneIds: List<Long>): List<PhoneDbModel>

    @Query("SELECT * FROM PhoneDbModel WHERE id Like :id")
    fun findByIdSync(id: Long): PhoneDbModel

    @Query("SELECT * FROM PhoneDbModel WHERE name IN (:name)")
    fun getPhoneByNameSync(name: List<String>): List<PhoneDbModel>

    @Query("SELECT * FROM PhoneDbModel WHERE tag = :tag")
    fun getPhoneByTagSync(tag: String): List<PhoneDbModel>

    @Query("SELECT * FROM PhoneDbModel WHERE name LIKE :name")
    fun findByNameSync(name: String): PhoneDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(phoneDbModel: PhoneDbModel)

    @Insert
    fun insertAll(vararg phoneDbModel: PhoneDbModel)

    @Query("DELETE FROM PhoneDbModel WHERE id IN (:ids)")
    fun delete(ids: List<Long>)

    @Query("DELETE FROM PhoneDbModel WHERE id LIKE :id")
    fun delete(id: Long)
}