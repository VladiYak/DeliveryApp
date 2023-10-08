package com.vladiyak.deliveryapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.adapters.SearchAdapter
import com.vladiyak.deliveryapp.databinding.FragmentSearchBinding
import com.vladiyak.deliveryapp.utils.Resource
import com.vladiyak.deliveryapp.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val TAG = "SearchFragment"
    private lateinit var binding: FragmentSearchBinding
    private lateinit var inputMethodManger: InputMethodManager
    private lateinit var searchAdapter: SearchAdapter
    private var isKeyboardOpen = false

    private val viewModel: MainCategoryViewModel by lazy {
        ViewModelProvider(this)[MainCategoryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onHomeClick()
        setupSearchRecyclerView()
        showKeyboardAutomatically()

        searchProducts()
        observeSearch()
        onSearchTextClick()
    }

    private fun onHomeClick() {
        val btm = activity?.findViewById<BottomNavigationView>(R.id.bottomNavMenu)
        btm?.menu?.getItem(0)?.setOnMenuItemClickListener {
            activity?.onBackPressed()
            true
        }
    }

    private fun setupSearchRecyclerView() {
        searchAdapter = SearchAdapter()
        binding.rvSearch.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun showKeyboardAutomatically() {
        inputMethodManger =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManger.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
        binding.edSearch.requestFocus()
    }

    var job: Job? = null
    private fun searchProducts() {
        binding.edSearch.addTextChangedListener { query ->
            val queryTrim = query.toString().trim()
            if (queryTrim.isNotEmpty()) {
                val searchQuery = query.toString().substring(0, 1).toUpperCase()
                    .plus(query.toString().substring(1))
                job?.cancel()
                job = CoroutineScope(Dispatchers.IO).launch {
                    delay(500L)
                    viewModel.searchProducts(searchQuery)
                }
            } else {
                searchAdapter.differ.submitList(emptyList())
            }
        }
    }

    private fun onSearchTextClick() {
        searchAdapter.onItemClick = { product ->
            val bundle = Bundle()
            bundle.putParcelable("product", product)

            val imm =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(requireView().windowToken, 0)

            findNavController().navigate(
                R.id.action_searchFragment_to_productDetailsFragment,
                bundle
            )
        }
    }

    private fun observeSearch() {
        viewModel.search.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Loading -> {
                    Log.d(TAG, "Search loading...")
                    binding.tvNoResults.visibility = View.GONE
                    binding.emptyOrder.visibility = View.GONE
                    return@Observer
                }

                is Resource.Success -> {
                    val products = response.data
                    if (products != null) {
                        Log.d(TAG, "Search success. Found ${products.size} products.")
                        binding.tvNoResults.visibility =
                            if (products.isEmpty()) View.VISIBLE else View.GONE
                        if (products.isEmpty()) {
                            binding.emptyOrder.visibility = View.VISIBLE
                            binding.emptyOrder.repeatCount = LottieDrawable.INFINITE
                            binding.emptyOrder.playAnimation()
                        } else {
                            binding.emptyOrder.visibility = View.GONE
                        }
                    }
                    searchAdapter.differ.submitList(products)
                    return@Observer
                }

                is Resource.Error -> {
                    Log.e(TAG, "Search error: ${response.message}")
                    binding.tvNoResults.visibility = View.GONE
                    binding.emptyOrder.visibility = View.GONE
                    return@Observer
                }

                else -> Unit
            }
        })
    }

    override fun onResume() {
        super.onResume()

        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottomNavMenu)
        bottomNav?.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.edSearch.clearFocus()
    }

    override fun onPause() {
        super.onPause()

        if (isKeyboardOpen) {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            isKeyboardOpen = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.edSearch.clearFocus()
        if (isKeyboardOpen) {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            isKeyboardOpen = false
        }

        binding.emptyOrder.cancelAnimation()
    }
}