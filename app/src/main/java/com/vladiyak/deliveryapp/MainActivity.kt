package com.vladiyak.deliveryapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.vladiyak.deliveryapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        lifecycleScope.launchWhenCreated { println(FirebaseMessaging.getInstance().token.await()) }
        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener {
            // get token
            val token = it.result.token
            Log.d("Tokennn", token)

        }

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.shoppingHostFragment
        ) as NavHostFragment
        var navController = navHostFragment.navController

        // Setup the bottom navigation view with navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavMenu)

        bottomNavigationView.setupWithNavController(navController)

    }
}