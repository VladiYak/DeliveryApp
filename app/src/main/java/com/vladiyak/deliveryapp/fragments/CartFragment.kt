package com.vladiyak.deliveryapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.adapters.CartProductAdapter
import com.vladiyak.deliveryapp.data.CartProduct
import com.vladiyak.deliveryapp.databinding.FragmentCartBinding
import com.vladiyak.deliveryapp.firebase.FirebaseCommon
import com.vladiyak.deliveryapp.utils.ItemDecorationVertical
import com.vladiyak.deliveryapp.utils.Resource
import com.vladiyak.deliveryapp.utils.showBottomNavigationView
import com.vladiyak.deliveryapp.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductAdapter() }
    private val viewModel by activityViewModels<CartViewModel>()
    private lateinit var lottieEmptyCart: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRv()

        lottieEmptyCart = binding.imgEmptyBox

        var totalPrice = 0f

        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest { price ->
                if (price != null) {
                    totalPrice = price
                }
                binding.tvTotalprice.text = "TK. ${String.format("%.1f", totalPrice)}"
            }
        }

        cartAdapter.onProductClick = {
            val b = Bundle().apply { putParcelable("product", it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, b)
        }

        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }

        binding.imageCartClose.setOnClickListener {
            findNavController().navigateUp()
        }

//        binding.btnCheckout.setOnClickListener {
//            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(
//                totalPrice,
//                cartAdapter.differ.currentList.toTypedArray(),
//                true
//            )
//            findNavController().navigate(action)
//        }


        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest { cartProductToDelete ->
                val alertDialogBuilder =
                    androidx.appcompat.app.AlertDialog.Builder(requireContext())
                val view = LayoutInflater.from(requireContext())
                    .inflate(R.layout.delete_alert_dialog, null, false)
                alertDialogBuilder.setView(view)

                val alertDialog = alertDialogBuilder.create()

                view.findViewById<Button>(R.id.btn_no).setOnClickListener {
                    alertDialog.dismiss()
                }

                view.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                    viewModel.deleteCartProduct(cartProductToDelete)
                    alertDialog.dismiss()
                }

                alertDialog.show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        if (it.data!!.isEmpty()) {
                            showEmptyCart()
                            hideOtherViews()
                        } else {
                            hideEmptyCart()
                            showOtherViews()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }

                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            linear.visibility = View.VISIBLE
            btnCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            linear.visibility = View.GONE
            btnCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            lottieEmptyCart.visibility = View.GONE
            tvEmptyCart.visibility = View.GONE
            lottieEmptyCart.cancelAnimation()
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            lottieEmptyCart.visibility = View.VISIBLE
            lottieEmptyCart.repeatCount = LottieDrawable.INFINITE
            lottieEmptyCart.playAnimation()
            tvEmptyCart.visibility = View.VISIBLE
        }
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(ItemDecorationVertical())
        }

        cartAdapter.onDeleteClick = {
            showDeleteAlertDialog(it)
        }
    }

    private fun showDeleteAlertDialog(item: CartProduct) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.delete_alert_dialog, null, false)
        alertDialogBuilder.setView(view)

        val alertDialog = alertDialogBuilder.create()

        val btnNo = view.findViewById<Button>(R.id.btn_no)
        val btnYes = view.findViewById<Button>(R.id.btn_yes)

        btnNo.setOnClickListener {
            alertDialog.dismiss()
        }

        btnYes.setOnClickListener {
            deleteCartItem(item)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun deleteCartItem(item: CartProduct) {
        viewModel.deleteCartProduct(item)
    }

    override fun onResume() {
        super.onResume()

        showBottomNavigationView()
    }

}