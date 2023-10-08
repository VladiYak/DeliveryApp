package com.vladiyak.deliveryapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val addressTitle: String,
    val fullName: String,
    val area: String,
    val phone: String,
    val city: String,
) : Parcelable {
    constructor() : this("", "", "", "", "")
}
