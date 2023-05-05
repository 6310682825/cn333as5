package com.example.phonebook.viewmodel


import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phonebook.database.AppDatabase
import com.example.phonebook.database.DbMapper
import com.example.phonebook.database.Repository
import com.example.phonebook.domain.model.ColorModel
import com.example.phonebook.domain.model.PhoneModel
import com.example.phonebook.routing.PhoneBooksRouter
import com.example.phonebook.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : ViewModel() {
    val phonesNotInTrash: LiveData<List<PhoneModel>> by lazy {
        repository.getAllPhonesNotInTrash()
    }

    private var _phoneEntry = MutableLiveData(PhoneModel())
    private val _sortState = MutableStateFlow(SearchDbState())

    val phoneEntry: LiveData<PhoneModel> = _phoneEntry
    val sortState: StateFlow<SearchDbState> = _sortState.asStateFlow()

    val colors: LiveData<List<ColorModel>> by lazy {
        repository.getAllColors()
    }

    val phonesInTrash by lazy { repository.getAllPhonesInTrash() }

    private var _selectedPhones = MutableLiveData<List<PhoneModel>>(listOf())

    val selectedPhones: LiveData<List<PhoneModel>> = _selectedPhones

    private val repository: Repository

    val searchList = listOf("Default", "Mobile", "Family", "Home", "Office", "Other")

    init {
        val db = AppDatabase.getInstance(application)
        repository = Repository(db.PhoneDao(), db.colorDao(), DbMapper())
    }

    fun onCreateNewPhoneClick() {
        Log.i("Navigate", "Navigated to save phone screen")
        _phoneEntry.value = PhoneModel()
        PhoneBooksRouter.navigateTo(Screen.SavePhone)
    }

    fun onPhoneClick(phone: PhoneModel) {
        Log.i("Navigate", "Navigated to save phone screen for " + phone.name)
        _phoneEntry.value = phone
        PhoneBooksRouter.navigateTo(Screen.SavePhone)
    }

    fun onPhoneCheckedChange(phone: PhoneModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertPhone(phone)
        }
    }

    fun onPhoneSelected(phone: PhoneModel) {
        _selectedPhones.value = _selectedPhones.value!!.toMutableList().apply {
            if (contains(phone)) {
                remove(phone)
            } else {
                add(phone)
            }
        }
    }

    fun restorePhones(phones: List<PhoneModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.restorePhonesFromTrash(phones.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedPhones.value = listOf()
            }
        }
    }

    fun permanentlyDeletePhones(phones: List<PhoneModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deletePhones(phones.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedPhones.value = listOf()
            }
        }
    }

    fun onPhoneEntryChange(phone: PhoneModel) {
        Log.i("Item Change", "Item changed in " + phone.name)
        _phoneEntry.value = phone
    }

    fun savePhone(phone: PhoneModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertPhone(phone)

            withContext(Dispatchers.Main) {
                PhoneBooksRouter.navigateTo(Screen.Phones)

                _phoneEntry.value = PhoneModel()
            }
        }
    }

    fun movePhoneToTrash(phone: PhoneModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.movePhoneToTrash(phone.id)

            withContext(Dispatchers.Main) {
                PhoneBooksRouter.navigateTo(Screen.Phones)
            }
        }
    }

    fun sortBy(i: Int) {
        Log.i("Sort with ID: ", i.toString())
        _sortState.update {it.copy(search_by_tag = searchList[i])}
    }
}