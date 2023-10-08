package com.vladiyak.deliveryapp.fragments.categories

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.adapters.PopularNowAdapter
import com.vladiyak.deliveryapp.adapters.SpecialProductsAdapter
import com.vladiyak.deliveryapp.adapters.SpicyOfferAdapter
import com.vladiyak.deliveryapp.databinding.FragmentMainCategoryBinding
import com.vladiyak.deliveryapp.utils.ItemDecorationVertical
import com.vladiyak.deliveryapp.utils.Resource
import com.vladiyak.deliveryapp.utils.showBottomNavigationView
import com.vladiyak.deliveryapp.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

private val TAG = "MainCategoryFragment"

@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {

    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductAdapter: SpecialProductsAdapter
    private lateinit var spicyOfferAdapter: SpicyOfferAdapter
    private lateinit var popularNowAdapter: PopularNowAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()
    private lateinit var lottieLoading: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()
        setupSpicyOfferRv()
        setupPopularNowRv()

        lottieLoading = binding.lottieLoading
        lottieLoading.visibility = View.GONE

        specialProductAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_menuFragment_to_productDetailsFragment, b)
        }

        spicyOfferAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_menuFragment_to_productDetailsFragment, b)
        }

        popularNowAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_menuFragment_to_productDetailsFragment, b)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.specialProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        specialProductAdapter.differ.submitList(it.data)
                        hideLoading()
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.spicyOffer.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        spicyOfferAdapter.differ.submitList(it.data)
                        hideLoading()
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.popularNow.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        popularNowAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        binding.nestedScrollViewFoodyCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                viewModel.fetchPopularNow()
            }
        })
    }

    private fun setupPopularNowRv() {
        popularNowAdapter = PopularNowAdapter()
        binding.rvPopularNow.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = popularNowAdapter
            addItemDecoration(ItemDecorationVertical())
        }
    }

    private fun setupSpicyOfferRv() {
        spicyOfferAdapter = SpicyOfferAdapter()
        binding.rvSpicyOffer.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = spicyOfferAdapter
        }
    }

    private fun showLoading() {
        binding.foodyCategoryProgressbar.visibility = View.GONE
        lottieLoading.visibility = View.VISIBLE
        lottieLoading.repeatCount = LottieDrawable.INFINITE
        lottieLoading.playAnimation()
    }

    private fun hideLoading() {
        binding.foodyCategoryProgressbar.visibility = View.GONE
        lottieLoading.visibility = View.GONE
        lottieLoading.cancelAnimation()
    }

    private fun setupSpecialProductsRv() {
        specialProductAdapter = SpecialProductsAdapter()
        binding.rvSpacialOffer.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        showBottomNavigationView()
    }

}