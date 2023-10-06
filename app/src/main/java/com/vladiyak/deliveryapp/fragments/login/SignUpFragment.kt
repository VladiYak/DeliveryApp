package com.vladiyak.deliveryapp.fragments.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.data.User
import com.vladiyak.deliveryapp.databinding.FragmentSignUpBinding
import com.vladiyak.deliveryapp.utils.RegisterValidation
import com.vladiyak.deliveryapp.utils.Resource
import com.vladiyak.deliveryapp.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SignUpFragment: Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.haveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.apply {
            signUpBtnSignUpPage.setOnClickListener {
                val user = User(
                    nameEtSignUpPage.text.toString().trim(),
                    emailEtSignUpPage.text.toString().trim()
                )
                val password = PassEtSignUpPage.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.signUpBtnSignUpPage
                    }
                    is Resource.Success -> {
                        Log.d("test", it.data.toString())
                        binding.signUpBtnSignUpPage
                    }
                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        binding.signUpBtnSignUpPage
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect {validation ->
                if(validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.emailEtSignUpPage.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if(validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.PassEtSignUpPage.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }

            }
        }
    }
    companion object {
        private const val TAG = "SignUpFragment"
    }
}