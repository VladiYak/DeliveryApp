package com.vladiyak.deliveryapp.data

sealed class Category(val category: String) {
    object Burger : Category("Burger")
    object Pizza : Category("Pizza")
    object Fries : Category("Fries")
    object Pasta : Category("Pasta")
}
