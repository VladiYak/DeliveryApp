package com.vladiyak.deliveryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.vladiyak.deliveryapp.data.Category
import com.vladiyak.deliveryapp.data.Product
import com.vladiyak.deliveryapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProduct = _specialProducts.asStateFlow()

    private val _popularNow = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val popularNow = _popularNow.asStateFlow()

    init {
        fetchSpecialProducts()
        fetchPopularNow()
    }

    fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", category.category)
            .whereNotEqualTo("offerPercentage", null).get()
            .addOnSuccessListener {
                val product = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(product))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchPopularNow() {
        viewModelScope.launch {
            _popularNow.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", category.category)
            .whereEqualTo("offerPercentage", null).get()
            .addOnSuccessListener {
                val product = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _popularNow.emit(Resource.Success(product))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _popularNow.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}