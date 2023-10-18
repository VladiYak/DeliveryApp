package com.vladiyak.deliveryapp.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.adapters.OrderHistoryAdapter
import com.vladiyak.deliveryapp.databinding.FragmentOrderHistoryBinding
import com.vladiyak.deliveryapp.utils.Resource
import com.vladiyak.deliveryapp.utils.hideKeyboard
import com.vladiyak.deliveryapp.utils.showBottomNavigationView
import com.vladiyak.deliveryapp.viewmodel.OrderHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OrderHistoryFragment : Fragment() {

    private lateinit var binding: FragmentOrderHistoryBinding
    val viewModel by viewModels<OrderHistoryViewModel>()
    val ordersAdapter by lazy { OrderHistoryAdapter() }
    private lateinit var lottieLoading: LottieAnimationView
    private lateinit var lottieEmptyOrder: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderHistoryBinding.inflate(inflater)
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottomNavMenu)
        bottomNavigation?.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onHomeClick()

        setupOrdersRv()

        lottieEmptyOrder = binding.emptyOrder
        lottieLoading = binding.lottieLoading
        lottieLoading.visibility = View.GONE

        lifecycleScope.launchWhenStarted {
            viewModel.allOrders.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        hideLoading()
                        ordersAdapter.differ.submitList(it.data)
                        if (it.data.isNullOrEmpty()) {
                            showEmptyOrder()
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        hideLoading()
                    }

                    else -> Unit
                }
            }
        }

        ordersAdapter.onClick = {
            val action =
                OrderHistoryFragmentDirections.actionOrderHistoryFragmentToOrderDetailsFragment(it)
            findNavController().navigate(action)
        }

        binding.imageCloseOrders.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showLoading() {
        binding.progressbarAllOrders.visibility = View.GONE
        lottieLoading.visibility = View.VISIBLE
        lottieLoading.repeatCount = LottieDrawable.INFINITE
        lottieLoading.playAnimation()
    }

    private fun hideLoading() {
        binding.progressbarAllOrders.visibility = View.GONE
        lottieLoading.visibility = View.GONE
        lottieLoading.cancelAnimation()
    }

    private fun showEmptyOrder() {
        binding.apply {
            lottieEmptyOrder.visibility = View.VISIBLE
            tvEmptyOrders.visibility = View.VISIBLE
            lottieEmptyOrder.repeatCount = LottieDrawable.INFINITE
            lottieEmptyOrder.playAnimation()
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

    private fun setupOrdersRv() {
        binding.rvAllOrders.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.emptyOrder.cancelAnimation()
    }



    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}