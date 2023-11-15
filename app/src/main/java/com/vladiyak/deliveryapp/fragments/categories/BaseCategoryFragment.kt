package com.noctambulist.foody.fragments.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.adapters.PopularNowAdapter
import com.vladiyak.deliveryapp.adapters.SpecialProductsAdapter
import com.vladiyak.deliveryapp.databinding.FragmentBaseCategoryBinding
import com.vladiyak.deliveryapp.utils.showBottomNavigationView

open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {

    private lateinit var binding: FragmentBaseCategoryBinding
    protected val specialProductsAdapter: SpecialProductsAdapter by lazy { SpecialProductsAdapter() }
    protected val popularNowAdapter: PopularNowAdapter by lazy { PopularNowAdapter() }
    private lateinit var lottieLoading: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()
        setupPopularNowRv()

        lottieLoading = binding.lottieLoading

        popularNowAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_menuFragment_to_productDetailsFragment, b)
        }

        specialProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_menuFragment_to_productDetailsFragment, b)
        }

        binding.rvSpacialOffer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollHorizontally(1) && dx != 0) {
                    onSpecialProductPagingRequest()
                }
            }
        })

        binding.nestedScrollViewBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                onPopularNowPagingRequest()
            }
        })
    }

    fun showPopularNowLoading() {
        binding.popularNowBaseCategoryProgressbar.visibility = View.GONE
        lottieLoading.visibility = View.VISIBLE
        lottieLoading.repeatCount = LottieDrawable.INFINITE
        lottieLoading.playAnimation()
    }

    fun hidePopularNowLoading() {
        binding.popularNowBaseCategoryProgressbar.visibility = View.GONE
        lottieLoading.visibility = View.GONE
        lottieLoading.cancelAnimation()
    }

    open fun onSpecialProductPagingRequest() {

    }

    open fun onPopularNowPagingRequest() {

    }

    private fun setupPopularNowRv() {
        binding.rvPopularNow.apply {
            layoutManager =
                GridLayoutManager(requireActivity(), 3, GridLayoutManager.VERTICAL, false)
            adapter = popularNowAdapter
        }
    }

    private fun setupSpecialProductsRv() {
        binding.rvSpacialOffer.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        showBottomNavigationView()
    }

}