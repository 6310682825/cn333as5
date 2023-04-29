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

    // Working Notes
    private val notesNotInTrashLiveData: MutableLiveData<List<PhoneModel>> by lazy {
        MutableLiveData<List<PhoneModel>>()
    }

    fun getAllNotesNotInTrash(): LiveData<List<PhoneModel>> = notesNotInTrashLiveData

    // Deleted Notes
    private val notesInTrashLiveData: MutableLiveData<List<PhoneModel>> by lazy {
        MutableLiveData<List<PhoneModel>>()
    }

    fun getAllNotesInTrash(): LiveData<List<PhoneModel>> = notesInTrashLiveData

    init {
        initDatabase(this::updateNotesLiveData)
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

            // Prepopulate notes
            val phone = PhoneDbModel.DEFAULT_PHONE_BOOK.toTypedArray()
            val dbNotes = PhoneDao.getAllSync()
            if (dbNotes.isNullOrEmpty()) {
                PhoneDao.insertAll(*phone)
            }

            postInitAction.invoke()
        }
    }

    // get list of working notes or deleted notes
    private fun getAllNotesDependingOnTrashStateSync(isDeleted: Boolean): List<PhoneModel> {
        val colorDbModels: Map<Long, ColorDbModel> = colorDao.getAllSync().map { it.id to it }.toMap()
        val dbNotes: List<PhoneDbModel> =
            PhoneDao.getAllSync().filter { it.isDeleted == isDeleted }
        return dbMapper.mapNotes(dbNotes, colorDbModels)
    }

    fun insertNote(note: PhoneModel) {
        PhoneDao.insert(dbMapper.mapDbNote(note))
        updateNotesLiveData()
    }

    fun deletePhone(PhoneId: Long) {
        PhoneDao.delete(PhoneId)
        updateNotesLiveData()
    }

    fun moveNoteToTrash(noteId: Long) {
        val dbNote = PhoneDao.findByIdSync(noteId)
        val newDbNote = dbNote.copy(isInTrash = true)
        PhoneDao.insert(newDbNote)
        updateNotesLiveData()
    }

    fun restoreNotesFromTrash(noteIds: List<Long>) {
        val dbNotesInTrash = PhoneDao.getNotesByIdsSync(noteIds)
        dbNotesInTrash.forEach {
            val newDbNote = it.copy(isDeleted = false)
            PhoneDao.insert(newDbNote)
        }
        updateNotesLiveData()
    }

    fun getAllColors(): LiveData<List<ColorModel>> =
        Transformations.map(colorDao.getAll()) { dbMapper.mapColors(it) }

    private fun updateNotesLiveData() {
        notesNotInTrashLiveData.postValue(getAllNotesDependingOnTrashStateSync(false))
        notesInTrashLiveData.postValue(getAllNotesDependingOnTrashStateSync(true))
    }
}