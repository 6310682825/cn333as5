package com.example.phonebook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phonebook.domain.model.PhoneModel
import com.example.phonebook.util.fromHex

@ExperimentalMaterialApi
@Composable
fun Phone(
    modifier: Modifier = Modifier,
    phone: PhoneModel,
    onPhoneClick: (PhoneModel) -> Unit = {},
    onPhoneCheckedChange: (PhoneModel) -> Unit = {},
    isSelected: Boolean
) {
    val background = if (isSelected)
        Color.LightGray
    else
        Color.fromHex(phone.color.color_code)

    Card(
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
            .padding(6.dp)
            .fillMaxWidth()
            .height(116.dp),
        backgroundColor = background
    ) {
        Column(modifier = Modifier.clickable {
            onPhoneClick.invoke(phone)
        }) {
            ListItem(
                text = { Text(text = phone.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(top = 18.dp)
                    ) },
                secondaryText = {
                    Text(text =  phone.tag, maxLines = 1)
                },
            )
            Text(text = phone.phoneNumber,
                fontSize = 24.sp
                ,modifier = modifier.padding(start = 14.dp))
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun PhonePreview() {
    Phone(phone = PhoneModel(1, "Phone 1", "Content 1", "Family"), isSelected = true)
}