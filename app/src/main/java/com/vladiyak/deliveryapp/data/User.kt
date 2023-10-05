package com.vladiyak.deliveryapp.data

data class User(
    val fullName: String,
    val email: String,
    val password: String,
    var imagePath: String = ""
) {
    constructor(): this("", "", "", "")
}
