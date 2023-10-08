package com.vladiyak.deliveryapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.data.Address
import com.vladiyak.deliveryapp.databinding.FragmentAddressBinding
import com.vladiyak.deliveryapp.utils.Resource
import com.vladiyak.deliveryapp.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding
    val viewModel by viewModels<AddressViewModel>()
    val args by navArgs<AddressFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.addAddress.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater)
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottomNavMenu)
        bottomNavigation?.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lottieAddress.repeatCount = LottieDrawable.INFINITE
        binding.lottieAddress.playAnimation()

        val address = args.address
        if (address == null) {
            Toast.makeText(requireContext(), "No address provided.", Toast.LENGTH_SHORT).show()

        } else {
            binding.apply {
                edAddressTitle.setText(address.addressTitle)
                edFullName.setText(address.fullName)
                edArea.setText(address.area)
                edPhone.setText(address.phone)
                edCity.setText(address.city)
            }
        }

        binding.buttonDelelte.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.imageAddressClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            buttonSave.setOnClickListener {
                val addressTitle = edAddressTitle.text.toString()
                val fullName = edFullName.text.toString()
                val area = edArea.text.toString()
                val phone = edPhone.text.toString()
                val city = edCity.text.toString()
                val address = Address(addressTitle, fullName, area, phone, city)
                viewModel.addAddress(address)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.lottieAddress.cancelAnimation()
    }
}

