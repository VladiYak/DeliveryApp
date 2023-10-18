package com.vladiyak.deliveryapp.fragments

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.noctambulist.foody.fragments.categories.BurgerCategoryFragment
import com.noctambulist.foody.fragments.categories.FriesCategoryFragment
import com.noctambulist.foody.fragments.categories.PastaCategoryFragment
import com.noctambulist.foody.fragments.categories.PizzaCategoryFragment
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.adapters.MenuViewPagerAdapter
import com.vladiyak.deliveryapp.data.Product
import com.vladiyak.deliveryapp.databinding.FragmentMenuBinding
import com.vladiyak.deliveryapp.fragments.categories.MainCategoryFragment
import com.vladiyak.deliveryapp.utils.hideKeyboard

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

        onHomeClick()

        binding.searchView.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_searchFragment)
        }

        val categoriesFragment = arrayListOf<Fragment>(
            MainCategoryFragment(),
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
                    tab.text = "Главная"
                }

                1 -> {
                    tab.text = "Бургеры"
                }

                2 -> {
                    tab.text = "Пицца"
                }

                3 -> {
                    tab.text = "Фрайсы"
                }

                4 -> {
                    tab.text = "Паста"
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

    private fun onHomeClick() {
        val btm = activity?.findViewById<BottomNavigationView>(R.id.bottomNavMenu)
        btm?.menu?.getItem(0)?.setOnMenuItemClickListener {
            activity?.onBackPressed()
            true
        }
        hideKeyboard()
    }

    private fun exitApp() {
        activity?.finish()
    }

}