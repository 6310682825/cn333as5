package com.example.phonebook.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phonebook.routing.PhoneBooksRouter
import com.example.phonebook.routing.Screen
import com.example.phonebook.viewmodel.MainViewModel
import com.example.phonebook.R
import com.example.phonebook.domain.model.ColorModel
import com.example.phonebook.domain.model.NEW_PHONE_ID
import com.example.phonebook.domain.model.PhoneModel
import com.example.phonebook.ui.components.PhoneColor
import com.example.phonebook.util.fromHex
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun SavePhoneScreen(viewModel: MainViewModel) {
    val phoneEntry by viewModel.phoneEntry.observeAsState(PhoneModel())

    val colors: List<ColorModel> by viewModel.colors.observeAsState(listOf())

    val bottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val movePhoneToTrashDialogShownState = rememberSaveable { mutableStateOf(false) }

    BackHandler {
        if (bottomDrawerState.isOpen) {
            coroutineScope.launch { bottomDrawerState.close() }
        } else {
            PhoneBooksRouter.navigateTo(Screen.Phones)
        }
    }

    Scaffold(
        topBar = {
            val isEditingMode: Boolean = phoneEntry.id != NEW_PHONE_ID
            SavePhoneTopAppBar(
                isEditingMode = isEditingMode,
                onBackClick = { PhoneBooksRouter.navigateTo(Screen.Phones) },
                onSavePhoneClick = { viewModel.savePhone(phoneEntry) },
                onOpenColorPickerClick = {
                    coroutineScope.launch { bottomDrawerState.open() }
                },
                onDeletePhoneClick = {
                    movePhoneToTrashDialogShownState.value = true
                }
            )
        }
    ) {
        it
        BottomDrawer(
            drawerState = bottomDrawerState,
            drawerContent = {
                ColorPicker(
                    colors = colors,
                    onColorSelect = { color ->
                        viewModel.onPhoneEntryChange(phoneEntry.copy(color = color))
                    }
                )
            }
        ) {
            SavePhoneContent(
                phone = phoneEntry,
                onPhoneChange = { updatePhoneEntry ->
                    viewModel.onPhoneEntryChange(updatePhoneEntry)
                },
                colors = colors
            )
        }

        if (movePhoneToTrashDialogShownState.value) {
            AlertDialog(
                onDismissRequest = {
                    movePhoneToTrashDialogShownState.value = false
                },
                title = {
                    Text("Move this phone line to the trash?")
                },
                text = {
                    Text(
                        "You still can recover it afterward."
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.movePhoneToTrash(phoneEntry)
                    }) {
                        Text("Move To Trash")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        movePhoneToTrashDialogShownState.value = false
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SavePhoneTopAppBar(
    isEditingMode: Boolean,
    onBackClick: () -> Unit,
    onSavePhoneClick: () -> Unit,
    onOpenColorPickerClick: () -> Unit,
    onDeletePhoneClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Save Phone",
                color = MaterialTheme.colors.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        actions = {
            if (isEditingMode) {
                IconButton(onClick = onDeletePhoneClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Phone Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }

            IconButton(onClick = onSavePhoneClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save Phone Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    )
}

@Composable
private fun SavePhoneContent(
    phone: PhoneModel,
    onPhoneChange: (PhoneModel) -> Unit,
    colors: List<ColorModel>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ContentTextField(
            label = "Name",
            text = phone.name,
            keyboard = KeyboardOptions(keyboardType = KeyboardType.Text),
            onTextChange = { newName ->
                onPhoneChange.invoke(phone.copy(name = newName))
            },
        )

        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Number",
            text = phone.phoneNumber,
            keyboard = KeyboardOptions(keyboardType = KeyboardType.Number),
            onTextChange = { newNumber ->
                onPhoneChange.invoke(phone.copy(phoneNumber = newNumber))
            }
        )
        TagSelectionRadio(phone = phone, onPhoneChange = onPhoneChange, colors = colors)
    }
}

@Composable
private fun ContentTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    keyboard: KeyboardOptions,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        ),
        keyboardOptions = keyboard
    )
}

@Composable
private fun TagSelectionRadio(
    modifier: Modifier = Modifier,
    phone: PhoneModel,
    onPhoneChange: (PhoneModel) -> Unit,
    colors: List<ColorModel>
) {
    Log.i("Radio", "Working normally")
    val tagSelection = listOf("Mobile", "Family", "Home", "Office", "Other")
    val (tagSelected, onOptionSelected) = remember { mutableStateOf(tagSelection[tagSelection.indexOf(phone.tag)]) }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {
        Text(text = "Select tag", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        tagSelection.forEach { text ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .selectable(selected = (text == tagSelected), onClick = { onOptionSelected(text) })
                .padding(horizontal = 16.dp)) {
                RadioButton(selected = (text == tagSelected),
                    onClick = {
                        onOptionSelected(text)
                    })
                Text(text = text, Modifier.padding(start = 8.dp))
            }
        }
    }
    onPhoneChange.invoke(phone.copy(tag = tagSelected, color = GetColor(colors, tagSelected)))
}

private fun GetColor(
    colors: List<ColorModel>,
    selectedColor: String
): ColorModel {
    var pickedColor: ColorModel = ColorModel.DEFAULT
    colors.forEach() {
        if (it.type == selectedColor) {
            pickedColor = it
        }
    }
    Log.i("Picked color", pickedColor.color)
    return pickedColor
}

@Composable
private fun ColorPicker(
    colors: List<ColorModel>,
    onColorSelect: (ColorModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Color picker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(colors.size) { itemIndex ->
                val color = colors[itemIndex]
                ColorItem(
                    color = color,
                    onColorSelect = onColorSelect
                )
            }
        }
    }
}

@Composable
fun ColorItem(
    color: ColorModel,
    onColorSelect: (ColorModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onColorSelect(color)
                }
            )
    ) {
        PhoneColor(
            modifier = Modifier.padding(10.dp),
            color = Color.fromHex(color.color_code),
            size = 80.dp,
            border = 2.dp
        )
        Text(
            text = color.color,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview
@Composable
fun ColorItemPreview() {
    ColorItem(ColorModel.DEFAULT) {}
}

@Preview
@Composable
fun ColorPickerPreview() {
    ColorPicker(
        colors = listOf(
            ColorModel.DEFAULT,
            ColorModel.DEFAULT,
            ColorModel.DEFAULT
        )
    ) { }
}