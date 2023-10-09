package com.vladiyak.deliveryapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.adapters.HelpAdapter
import com.vladiyak.deliveryapp.data.HelpSection
import com.vladiyak.deliveryapp.databinding.FragmentHelpBinding


class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    private val helpSections = listOf(
        HelpSection(
            "What is Foody?",
            "Foody is a fast food delivery app that allows you to order your favorite meals from local restaurants and have them delivered to your doorstep."
        ),
        HelpSection(
            "Why is the app named Foody?",
            "The name 'Foody' was chosen to reflect our focus on delivering delicious meals and satisfying cravings. We believe it captures the essence of enjoying a variety of foods conveniently and with a touch of fun. Our goal is to provide a memorable food delivery experience that brings joy to your taste buds!"
        ),
        HelpSection(
            "How do I place an order on Foody?",
            "To place an order, simply download the Foody app, create an account, browse the menu of available restaurants, select your items, and proceed to checkout."
        ),
        HelpSection(
            "Can I customize my order?",
            "Yes, you can customize your order by adding special instructions for each item. For example, you can request extra toppings or ask for specific modifications."
        ),
        HelpSection(
            "What types of payment do you accept?",
            "Currently, we only accept cash on delivery as a payment method."
        ),
        HelpSection(
            "How long does delivery take?",
            "Delivery times vary depending on your location and the restaurant's preparation time. You can track your order's progress in the app."
        ),
        HelpSection(
            "Is there a delivery fee?",
            "Yes, there may be a delivery fee based on your location and order total. The fee will be displayed during checkout."
        ),
        HelpSection(
            "What if I have dietary restrictions?",
            "Foody offers a variety of options including vegetarian, vegan, and gluten-free dishes. You can filter menu items by your dietary preferences."
        ),
        HelpSection(
            "Can I schedule a future order?",
            "Absolutely! Foody allows you to schedule orders for a specific date and time. Just select the desired delivery time during checkout."
        ),
        HelpSection(
            "How can I contact customer support?",
            "You can reach our customer support team by tapping the 'Help' or 'Contact Us' option in the app's settings. We're here to assist you!"
        ),
        HelpSection(
            "Do you offer promotions or discounts?",
            "Currently, we don't offer promotions or discounts, but in the near future, Foody plans to provide regular promotions, discounts, and special deals. Keep an eye on the app for the latest offers."
        ),
        HelpSection(
            "What if I need to cancel my order?",
            "You can cancel your order through the app before it's been prepared by the restaurant. However, cancellation policies may vary."
        ),
        HelpSection(
            "Why Should I Use the Foody App?",
            "Choosing the Foody app means gaining access to a seamless and convenient way to satisfy your cravings and explore a variety of cuisines. With Foody, you can:\n\n- Easily browse menus from local restaurants\n- Customize your orders to match your preferences\n- Track your order's progress in real time\n- Schedule future orders for added convenience\n- Enjoy a range of dietary options, from vegetarian to gluten-free\n\nWe're dedicated to delivering not only delicious meals but also a user-friendly experience that enhances your dining journey. Whether you're craving comfort food or something adventurous, Foody is here to make every meal special."
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        val recyclerView: RecyclerView = binding.recyclerView

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = HelpAdapter(helpSections)

        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottomNavMenu)
        bottomNavigation?.visibility = View.INVISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageHelpClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}