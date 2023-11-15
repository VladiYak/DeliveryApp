package com.vladiyak.deliveryapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.noctambulist.foody.adapters.AddressAdapter
import com.noctambulist.foody.adapters.BillingAdapter
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.data.Address
import com.vladiyak.deliveryapp.data.CartProduct
import com.vladiyak.deliveryapp.data.Order
import com.vladiyak.deliveryapp.data.OrderStatus
import com.vladiyak.deliveryapp.databinding.FragmentBillingBinding
import com.vladiyak.deliveryapp.utils.ItemDecorationHorizontal
import com.vladiyak.deliveryapp.utils.Resource
import com.vladiyak.deliveryapp.viewmodel.BillingViewModel
import com.vladiyak.deliveryapp.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BillingFragment : Fragment() {

    private lateinit var binding: FragmentBillingBinding
    private val billingAdapter by lazy { BillingAdapter() }
    private val addressAdapter by lazy { AddressAdapter() }
    private val args by navArgs<BillingFragmentArgs>()
    private val billingViewModel by viewModels<BillingViewModel>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f
    private val orderViewModel by viewModels<OrderViewModel>()
    private var selectedAddress: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        products = args.products.toList()
        totalPrice = args.totalPrice
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBillingBinding.inflate(inflater)
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottomNavMenu)
        bottomNavigation?.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lottieBilling.repeatCount = LottieDrawable.INFINITE
        binding.lottieBilling.playAnimation()

        setupAddressRV()
        setupBillingProductRv()

        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        binding.imageCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder
                    }

                    is Resource.Success -> {
                        binding.buttonPlaceOrder
                        findNavController().navigate(R.id.action_billingFragment_to_greetingsFragment)
                    }

                    is Resource.Error -> {
                        binding.buttonPlaceOrder
                        Toast.makeText(requireContext(), "Error ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            billingViewModel.address.collectLatest {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        addressAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Error ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit
                }
            }
        }

        billingAdapter.differ.submitList(products)
        binding.tvTotalPrice.text = "$$totalPrice"

        addressAdapter.onClick = {
            selectedAddress = it
        }

        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                Toast.makeText(
                    requireContext(),
                    "Пожалуйста укажите адрес перед оформлением заказа.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                showOrderConfirmationDialog()
            }
        }
    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(context).create()
        val view = LayoutInflater.from(context).inflate(R.layout.delete_alert_dialog, null, false)
        alertDialog.setView(view)
        val title = view.findViewById<TextView>(R.id.tv_delete_item)
        val message = view.findViewById<TextView>(R.id.tv_delete_message)
        val btnConfirm = view.findViewById<Button>(R.id.btn_yes)
        val btnCancel = view.findViewById<Button>(R.id.btn_no)
        title.text = "Подтверждение"
        message.text = "Вы уверены что хотите оформить заказ?"
        btnConfirm.text = "Подтвердить"
        btnCancel.text = "Отмена"

        btnConfirm.setOnClickListener {
            val order = Order(
                OrderStatus.Ordered.status,
                totalPrice,
                products,
                selectedAddress!!
            )
            orderViewModel.placeOrder(order)
            alertDialog.dismiss()
        }

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun setupAddressRV() {
        binding.rvAdresses.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = addressAdapter
            addItemDecoration(ItemDecorationHorizontal())
        }
    }

    private fun setupBillingProductRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = billingAdapter
            addItemDecoration(ItemDecorationHorizontal())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.lottieBilling.cancelAnimation()
    }
}

