package com.example.phonebook.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.phonebook.domain.model.ColorModel
import com.example.phonebook.domain.model.PhoneModel
import androidx.lifecycle.Transformations
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Repository(
    private val PhoneDao: PhoneDao,
    private val colorDao: ColorDao,
    private val dbMapper: DbMapper
) {

    // Working Phones
    private val phonesNotInTrashLiveData: MutableLiveData<List<PhoneModel>> by lazy {
        MutableLiveData<List<PhoneModel>>()
    }

    fun getAllPhonesNotInTrash(): LiveData<List<PhoneModel>> = phonesNotInTrashLiveData

    // Deleted Phones
    private val phonesInTrashLiveData: MutableLiveData<List<PhoneModel>> by lazy {
        MutableLiveData<List<PhoneModel>>()
    }

    fun getAllPhonesInTrash(): LiveData<List<PhoneModel>> = phonesInTrashLiveData

    init {
        initDatabase(this::updatePhonesLiveData)
    }

    /**
     * Populates database with colors if it is empty.
     */
    private fun initDatabase(postInitAction: () -> Unit) {
        GlobalScope.launch {
            // Prepopulate colors
            val colors = ColorDbModel.DEFAULT_COLORS.toTypedArray()
            val dbColors = colorDao.getAllSync()
            if (dbColors.isNullOrEmpty()) {
                colorDao.insertAll(*colors)
            }

            // Prepopulate phones
            val phone = PhoneDbModel.DEFAULT_PHONE_BOOK.toTypedArray()
            val dbPhones = PhoneDao.getAllSync()
            if (dbPhones.isNullOrEmpty()) {
                PhoneDao.insertAll(*phone)
            }

            postInitAction.invoke()
        }
    }

    fun insertPhone(phone: PhoneModel) {
        val colorDbModels: Map<String, ColorDbModel> = colorDao.getAllSync().map { it.type to it }.toMap()

        PhoneDao.insert(dbMapper.mapDbPhone(phone))
        updatePhonesLiveData()
    }

    private fun getAllPhonesDependingOnTrashStateSync(isDeleted: Boolean): List<PhoneModel> {
        val colorDbModels: Map<Long, ColorDbModel> = colorDao.getAllSync().map { it.id to it }.toMap()
        val dbPhones: List<PhoneDbModel> =
            PhoneDao.getAllSync().filter { it.isDeleted == isDeleted }
        return dbMapper.mapPhones(dbPhones, colorDbModels)
    }

    fun deletePhones(phoneIds: List<Long>) {
        PhoneDao.delete(phoneIds)
        updatePhonesLiveData()
    }
    fun movePhoneToTrash(phoneId: Long) {
        val dbPhone = PhoneDao.findByIdSync(phoneId)
        val newDbPhone = dbPhone.copy(isDeleted = true)
        PhoneDao.insert(newDbPhone)
        updatePhonesLiveData()
    }

    fun restorePhonesFromTrash(phoneIds: List<Long>) {
        val dbPhonesInTrash = PhoneDao.getPhoneByIdsSync(phoneIds)
        dbPhonesInTrash.forEach {
            val newDbPhone = it.copy(isDeleted = false)
            PhoneDao.insert(newDbPhone)
        }
        updatePhonesLiveData()
    }
    fun getAllColors(): LiveData<List<ColorModel>> =
        Transformations.map(colorDao.getAll()) { dbMapper.mapColors(it) }

    private fun updatePhonesLiveData() {
        phonesNotInTrashLiveData.postValue(getAllPhonesDependingOnTrashStateSync(false))
        phonesInTrashLiveData.postValue(getAllPhonesDependingOnTrashStateSync(true))
    }
}