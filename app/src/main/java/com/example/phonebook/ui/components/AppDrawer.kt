package com.example.phonebook.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phonebook.routing.PhoneBooksRouter
import com.example.phonebook.routing.Screen
import com.example.phonebook.ui.theme.PhoneBookTheme
import com.example.phonebook.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.phonebook.R

@Composable
private fun AppDrawerHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Image(
            imageVector = Icons.Filled.Menu,
            contentDescription = "Drawer Header Icon",
            colorFilter = ColorFilter
                .tint(MaterialTheme.colors.onSurface),
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Phone Book",
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
        )
    }
}

@Preview
@Composable
fun AppDrawerHeaderPreview() {
    PhoneBookTheme {
        AppDrawerHeader()
    }
}

@Composable
private fun ScreenNavigationButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colors

    // Define alphas for the image for two different states
    // of the button: selected/unselected
    val imageAlpha = if (isSelected) {
        1f
    } else {
        0.6f
    }

    // Define color for the text for two different states
    // of the button: selected/unselected
    val textColor = if (isSelected) {
        colors.primary
    } else {
        colors.onSurface.copy(alpha = 0.6f)
    }

    // Define color for the background for two different states
    // of the button: selected/unselected
    val backgroundColor = if (isSelected) {
        colors.primary.copy(alpha = 0.12f)
    } else {
        colors.surface
    }

    Surface( // 1
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp),
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Row( // 2
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onClick)
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Image(
                imageVector = icon,
                contentDescription = "Screen Navigation Button",
                colorFilter = ColorFilter.tint(textColor),
                alpha = imageAlpha
            )
            Spacer(Modifier.width(16.dp)) // 3
            Text(
                text = label,
                style = MaterialTheme.typography.body2,
                color = textColor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun ScreenNavigationButtonPreview() {
    PhoneBookTheme {
        ScreenNavigationButton(
            icon = Icons.Filled.Home,
            label = "Phones",
            isSelected = false,
            onClick = { }
        )
    }
}

@Composable
private fun LightDarkThemeItem() {
    Row(
        Modifier
            .padding(8.dp)
    ) {
        Text(
            text = "Turn on dark theme",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
                .align(alignment = Alignment.CenterVertically)
        )
        /*
        Switch(
            checked = MyPhonesThemeSettings.isDarkThemeEnabled,
            onCheckedChange = { MyPhonesThemeSettings.isDarkThemeEnabled = it },
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .align(alignment = Alignment.CenterVertically)
        )
         */
    }
}

@Preview
@Composable
fun LightDarkThemeItemPreview() {
    PhoneBookTheme {
        LightDarkThemeItem()
    }
}

@Composable
fun AppDrawer(
    currentScreen: Screen,
    closeDrawerAction: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppDrawerHeader()

        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))

        ScreenNavigationButton(
            icon = Icons.Filled.Home,
            label = "Phones",
            isSelected = currentScreen == Screen.Phones,
            onClick = {
                PhoneBooksRouter.navigateTo(Screen.Phones)
                viewModel.sortBy(0)
                closeDrawerAction()
            }
        )
        Text(text = "Search by tag",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            fontSize = 16.sp
        )
        ScreenNavigationButton(
            icon = ImageVector.vectorResource(R.drawable.baseline_smartphone_24),
            label = "Mobile Phone",
            isSelected = false,
            onClick = {
                PhoneBooksRouter.navigateTo(Screen.Phones)
                viewModel.sortBy(1)
                closeDrawerAction()
            }
        )
        ScreenNavigationButton(
            icon = ImageVector.vectorResource(R.drawable.baseline_family_restroom_24),
            label = "Family",
            isSelected = false,
            onClick = {
                PhoneBooksRouter.navigateTo(Screen.Phones)
                viewModel.sortBy(2)
                closeDrawerAction()
            }
        )
        ScreenNavigationButton(
            icon = ImageVector.vectorResource(R.drawable.baseline_home_24),
            label = "Home",
            isSelected = false,
            onClick = {
                PhoneBooksRouter.navigateTo(Screen.Phones)
                viewModel.sortBy(3)
                closeDrawerAction()
            }
        )
        ScreenNavigationButton(
            icon = ImageVector.vectorResource(R.drawable.baseline_apartment_24),
            label = "Office",
            isSelected = false,
            onClick = {
                PhoneBooksRouter.navigateTo(Screen.Phones)
                viewModel.sortBy(4)
                closeDrawerAction()
            }
        )
        ScreenNavigationButton(
            icon = ImageVector.vectorResource(R.drawable.baseline_menu_open_24),
            label = "Other",
            isSelected = false,
            onClick = {
                PhoneBooksRouter.navigateTo(Screen.Phones)
                viewModel.sortBy(5)
                closeDrawerAction()
            }
        )
        Text(text = "Deleted numbers",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            fontSize = 16.sp
        )
        ScreenNavigationButton(
            icon = Icons.Filled.Delete,
            label = "Trash",
            isSelected = currentScreen == Screen.Trash,
            onClick = {
                PhoneBooksRouter.navigateTo(Screen.Trash)
                closeDrawerAction()
            }
        )
    }
}

@Preview
@Composable
fun AppDrawerPreview() {
    PhoneBookTheme {
        AppDrawer(Screen.Phones, {})
    }
}