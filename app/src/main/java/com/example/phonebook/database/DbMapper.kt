package com.example.phonebook.database

import com.example.phonebook.domain.model.ColorModel
import com.example.phonebook.domain.model.NEW_PHONE_ID
import com.example.phonebook.domain.model.PhoneModel

class DbMapper {

    fun mapPhones(
        phoneDbModels: List<PhoneDbModel>,
        colorDbModels: Map<Long, ColorDbModel>
    ): List<PhoneModel> = phoneDbModels.map {
        val colorDbModel = colorDbModels[it.colorId]
            ?: throw RuntimeException("Color for colorId: ${it.colorId} was not found. Make sure that all colors are passed to this method")

        mapPhone(it, colorDbModel)
    }

    // convert PhoneDbModel to PhoneModel
    fun mapPhone(phoneDbModel: PhoneDbModel, colorDbModel: ColorDbModel): PhoneModel {
        val color = mapColor(colorDbModel)
        //val isCheckedOff = with(phoneDbModel) { if (canBeCheckedOff) isCheckedOff else null }
        return with(phoneDbModel) { PhoneModel(id, name, phoneNumber, tag, color) }
    }

    // convert list of ColorDdModels to list of ColorModels
    fun mapColors(colorDbModels: List<ColorDbModel>): List<ColorModel> =
        colorDbModels.map { mapColor(it) }

    // convert ColorDbModel to ColorModel
    fun mapColor(colorDbModel: ColorDbModel): ColorModel =
        with(colorDbModel) { ColorModel(id, colorCode, color, type) }

    // convert PhoneModel back to PhoneDbModel
    fun mapDbPhone(phone: PhoneModel): PhoneDbModel =
        with(phone) {

            if (id == NEW_PHONE_ID)
                PhoneDbModel(
                    name = name,
                    phoneNumber = phoneNumber,
                    tag = tag,
                    colorId = color.id,
                    isDeleted = false
                )
            else
                PhoneDbModel(id, name, phoneNumber, tag,  color.id, false)
        }

}