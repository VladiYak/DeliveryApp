package com.vladiyak.deliveryapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.databinding.FragmentGreetingsBinding

class GreetingsFragment : Fragment() {

    private lateinit var binding: FragmentGreetingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGreetingsBinding.inflate(inflater)
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottomNavMenu)
        bottomNavigation?.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lottieCongrats.visibility = View.VISIBLE
            lottieCongrats.repeatCount = LottieDrawable.INFINITE
            lottieCongrats.playAnimation()

            lottieCongratsBg.visibility = View.VISIBLE
            lottieCongratsBg.repeatCount = LottieDrawable.INFINITE
            lottieCongratsBg.playAnimation()
        }

        binding.buttonGoHome.setOnClickListener {
            findNavController().navigate(R.id.action_greetingsFragment_to_menuFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_greetingsFragment_to_menuFragment)
                }
            })
    }
}