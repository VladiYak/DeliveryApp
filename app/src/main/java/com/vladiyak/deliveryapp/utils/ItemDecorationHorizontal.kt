package com.vladiyak.deliveryapp.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.time.temporal.TemporalAmount

class ItemDecorationHorizontal(private val amount: Int = 20): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.right = amount
    }
}