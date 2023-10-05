package com.vladiyak.deliveryapp.data

data class User(
    val fullName: String,
    val email: String,
    var imagePath: String = ""
) {
    constructor(): this("", "", "", )
}
