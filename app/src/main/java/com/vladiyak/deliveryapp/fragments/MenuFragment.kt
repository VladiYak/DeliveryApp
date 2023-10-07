package com.vladiyak.deliveryapp.fragments

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.noctambulist.foody.fragments.categories.BurgerCategoryFragment
import com.noctambulist.foody.fragments.categories.FoodyCategoryFragment
import com.noctambulist.foody.fragments.categories.FriesCategoryFragment
import com.noctambulist.foody.fragments.categories.PastaCategoryFragment
import com.noctambulist.foody.fragments.categories.PizzaCategoryFragment
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.adapters.MenuViewPagerAdapter
import com.vladiyak.deliveryapp.data.Product
import com.vladiyak.deliveryapp.databinding.FragmentMenuBinding
import dagger.hilt.android.AndroidEntryPoint

class MenuFragment : Fragment(R.layout.fragment_menu) {

    private lateinit var binding: FragmentMenuBinding
    private val foundProducts = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.searchView.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
//        }

        val categoriesFragment = arrayListOf<Fragment>(
            FoodyCategoryFragment(),
            BurgerCategoryFragment(),
            PizzaCategoryFragment(),
            FriesCategoryFragment(),
            PastaCategoryFragment()
        )

        binding.viewpagerHome.isUserInputEnabled = false

        val viewPagerAdapter =
            MenuViewPagerAdapter(categoriesFragment, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Foody"
                    tab.setIcon(R.drawable.ic_foody)
                }

                1 -> {
                    tab.text = "Burger"
                    tab.setIcon(R.drawable.ic_burger)
                }

                2 -> {
                    tab.text = "Pizza"
                    tab.setIcon(R.drawable.ic_pizza)
                }

                3 -> {
                    tab.text = "Fries"
                    tab.setIcon(R.drawable.ic_fries)
                }

                4 -> {
                    tab.text = "Pasta"
                    tab.setIcon(R.drawable.ic_pasta)
                }
            }
        }.attach()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitApp()
                }
            })

        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
        binding.root.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                exitApp()
                true
            } else {
                false
            }
        }
    }

    private fun exitApp() {
        activity?.finish()
    }

}