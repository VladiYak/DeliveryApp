package com.vladiyak.deliveryapp.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottomNavMenu)
        bottomNavigation?.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(android.R.transition.move)

        val versionCode = try {
            val packageInfo = requireContext().packageManager.getPackageInfo(
                requireContext().packageName,
                0
            )
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            -1
        }

        binding.tvVersion.text = "Version ${versionCode}.0"

        binding.imageAboutClose.setOnClickListener {
            findNavController().navigateUp()
        }

        val typewriterDelay = 100L
        binding.tvDeveloperName.let {
            applyTypewriterEffect(
                it,
                it.text.toString(),
                typewriterDelay
            )
        }
    }

    private fun applyTypewriterEffect(textView: TextView, text: String, delay: Long) {
        val charArray = text.toCharArray()
        var currentIndex = 0

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentIndex <= charArray.size) {
                    val displayText = charArray.slice(0 until currentIndex).joinToString("")
                    textView.text = displayText
                    currentIndex++
                    handler.postDelayed(this, delay)
                }
            }
        }, delay)
    }
}
