package com.vladiyak.deliveryapp.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.vladiyak.deliveryapp.data.Product
import com.vladiyak.deliveryapp.firebase.FirebaseCommon
import com.vladiyak.deliveryapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodyCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseDatabase: FirebaseCommon
) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProduct: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _spicyOffer = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val spicyOffer: StateFlow<Resource<List<Product>>> = _spicyOffer

    private val _popularNow = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val popularNow: StateFlow<Resource<List<Product>>> = _popularNow

    private val pagingInfo = PagingInfo()

    val search = MutableLiveData<Resource<List<Product>>>()

    init {
        fetchSpecialProducts()
        fetchSpicyOffer()
        fetchPopularNow()
    }

    fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category", "Special Products").get()
            .addOnSuccessListener { result ->
                val specialProductList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchSpicyOffer() {
        viewModelScope.launch {
            _spicyOffer.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category", "Spicy Offer").get()
            .addOnSuccessListener { result ->
                val spicyOfferList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _spicyOffer.emit(Resource.Success(spicyOfferList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _spicyOffer.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchPopularNow() {
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _popularNow.emit(Resource.Loading())
            }
            firestore.collection("Products").limit(pagingInfo.popularNowPage * 10).get()
                .addOnSuccessListener { result ->
                    val popularNowList = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = popularNowList == pagingInfo.oldPopularNow
                    pagingInfo.oldPopularNow = popularNowList

                    viewModelScope.launch {
                        _popularNow.emit(Resource.Success(popularNowList))
                    }
                    pagingInfo.popularNowPage++
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _popularNow.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun searchProducts(searchQuery: String) {
        Log.d(ContentValues.TAG, "Searching for: $searchQuery")
        search.postValue(Resource.Loading())
        firebaseDatabase.searchProducts(searchQuery).addOnCompleteListener {
            if (it.isSuccessful) {
                val productsList = it.result!!.toObjects(Product::class.java)
                Log.d(ContentValues.TAG, "Search successful. Found ${productsList.size} products.")
                search.postValue(Resource.Success(productsList))
            } else {
                Log.e(ContentValues.TAG, "Search error: ${it.exception}")
                search.postValue(Resource.Error(it.exception.toString()))
            }
        }
    }

    internal data class PagingInfo(
        var popularNowPage: Long = 1,
        var oldPopularNow: List<Product> = emptyList(),
        var isPagingEnd: Boolean = false
    )
}