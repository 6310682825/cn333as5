package com.example.phonebook.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import com.example.phonebook.domain.model.PhoneModel
import com.example.phonebook.routing.Screen
import com.example.phonebook.ui.components.AppDrawer
import com.example.phonebook.ui.components.Phone
import com.example.phonebook.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun PhonesScreen(viewModel: MainViewModel) {
    val phones by viewModel.phonesNotInTrash.observeAsState(listOf())
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val sortUiSate by viewModel.sortState.collectAsState()

    var sortedPhones: List<PhoneModel>
    var screenHeader: String
    if (sortUiSate.search_by_tag != "Default") {
        sortedPhones = phones.filter { it.tag == sortUiSate.search_by_tag }
        screenHeader = "Tag: " + sortUiSate.search_by_tag
    }
    else {
        sortedPhones = phones
        screenHeader = "My Contracts"
    }
    sortedPhones = sortedPhones.sortedBy { it.name }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = screenHeader,
                        color = MaterialTheme.colors.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch { scaffoldState.drawerState.open() }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "Drawer Button"
                        )
                    }
                }
            )
        },
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Phones,
                closeDrawerAction = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onCreateNewPhoneClick() },
                contentColor = MaterialTheme.colors.background,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Phone Button"
                    )
                }
            )
        }
    ) {
        it
        if (sortedPhones.isNotEmpty()) {
            PhonesList(
                phones = sortedPhones,
                onPhoneCheckedChange = {
                    viewModel.onPhoneCheckedChange(it)
                },
                onPhoneClick = { viewModel.onPhoneClick(it) }
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun PhonesList(
    phones: List<PhoneModel>,
    onPhoneCheckedChange: (PhoneModel) -> Unit,
    onPhoneClick: (PhoneModel) -> Unit
) {
    LazyColumn {
        items(count = phones.size) { phoneIndex ->
            val phone = phones[phoneIndex]
            Phone(
                phone = phone,
                onPhoneClick = onPhoneClick,
                onPhoneCheckedChange = onPhoneCheckedChange,
                isSelected = false
            )
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun PhonesListPreview() {
    PhonesList(
        phones = listOf(
            PhoneModel(1, "Phone 1", "Content 1", "Family"),
            PhoneModel(2, "Phone 2", "Content 2", "Office"),
            PhoneModel(3, "Phone 3", "Content 3", "Mobile")
        ),
        onPhoneCheckedChange = {},
        onPhoneClick = {}
    )
}