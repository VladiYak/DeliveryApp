package com.vladiyak.deliveryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vladiyak.deliveryapp.data.Address
import com.vladiyak.deliveryapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _addAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addAddress = _addAddress.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun addAddress(address: Address) {
        val validateInputs = validateInputs(address)
        if (validateInputs) {
            viewModelScope.launch { _addAddress.emit(Resource.Loading()) }
            firestore.collection("user").document(auth.uid!!).collection("address").document()
                .set(address)
                .addOnSuccessListener {
                    viewModelScope.launch { _addAddress.emit(Resource.Success(address)) }
                }
                .addOnFailureListener {
                    viewModelScope.launch { _addAddress.emit(Resource.Error(it.message.toString())) }
                }
        } else {
            viewModelScope.launch { _error.emit("All fields are required.") }
        }
    }

    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.area.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty()
    }
}

