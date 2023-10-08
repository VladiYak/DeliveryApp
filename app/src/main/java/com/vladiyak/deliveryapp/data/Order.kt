package com.vladiyak.deliveryapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    val orderStatus: String = "",
    val totalPrice: Float = 0f,
    val product: List<CartProduct> = emptyList(),
    val address: Address? = null,
    val date: String = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH).format(Date()),
    val orderId: Long = nextLong(0, 100_000_000_000) + totalPrice.toLong()
) : Parcelable