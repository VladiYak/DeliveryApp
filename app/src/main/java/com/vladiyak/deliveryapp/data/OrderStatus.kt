package com.vladiyak.deliveryapp.data

sealed class OrderStatus(val status: String) {
    object Ordered : OrderStatus("Заказ оформлен")
    object Canceled : OrderStatus("Отменен")
    object Confirmed : OrderStatus("Подтвержден")
    object Shipped : OrderStatus("Отправлен")
    object Delivered : OrderStatus("Доставлен")
    object Returned : OrderStatus("Возвращен")
}

fun getOrderStatus(status: String): OrderStatus {
    return when (status) {
        "Заказ оформлен" -> {
            OrderStatus.Ordered
        }

        "Отменен" -> {
            OrderStatus.Canceled
        }

        "Подтвержден" -> {
            OrderStatus.Confirmed
        }

        "Отправлен" -> {
            OrderStatus.Shipped
        }

        "Доставлен" -> {
            OrderStatus.Delivered
        }

        else -> {
            OrderStatus.Returned
        }
    }
}