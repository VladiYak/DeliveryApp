package com.vladiyak.deliveryapp.utils

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vladiyak.deliveryapp.MainActivity
import com.vladiyak.deliveryapp.R

fun Fragment.hideBottomNavigationView() {
    val bottomNavigationView = (activity as MainActivity)
        .findViewById<BottomNavigationView>(R.id.bottomNavMenu)
    bottomNavigationView.visibility = View.GONE
}

fun Fragment.showBottomNavigationView() {
    val bottomNavigationView = (activity as MainActivity)
        .findViewById<BottomNavigationView>(R.id.bottomNavMenu)
    bottomNavigationView.visibility = View.VISIBLE
}