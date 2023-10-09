package com.vladiyak.deliveryapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.data.User
import com.vladiyak.deliveryapp.databinding.FragmentUserAccountBinding
import com.vladiyak.deliveryapp.dialog.setupBottomSheetDialog
import com.vladiyak.deliveryapp.utils.Resource
import com.vladiyak.deliveryapp.viewmodel.UserAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UserAccountFragment : Fragment() {

    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()
    private var imageUri: Uri? = null
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var lottieLoading: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                imageUri = it.data?.data
                Glide.with(this).load(imageUri).into(binding.imageUser)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountBinding.inflate(inflater)
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottomNavMenu)
        bottomNavigation?.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lottieLoading = binding.lottieLoading
        lottieLoading.visibility = View.GONE

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        hideUserLoading()
                        showUserInformation(it.data!!)
                    }

                    is Resource.Loading -> {
                        showUserLoading()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        binding.buttonSave
                        findNavController().navigateUp()
                    }

                    is Resource.Loading -> {
                        Toast.makeText(
                            requireContext(),
                            "Profile update successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.buttonSave
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonSave.setOnClickListener {
            binding.apply {
                val name = edName.text.toString().trim()
                val email = edEmail.text.toString().trim()
                val user = User(name, email)
                viewModel.updateUserInfo(user, imageUri)
            }
        }

        binding.tvUpdatePassword.setOnClickListener {
            setupBottomSheetDialog {

            }
        }

        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun showUserInformation(data: User) {
        binding.apply {
            Glide.with(this@UserAccountFragment).load(data.imagePath)
                .error(ColorDrawable(resources.getColor(R.color.black))).into(imageUser)
            edName.setText(data.fullName)
            edEmail.setText(data.email)
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            binding.progressbarAccount.visibility = View.GONE
            lottieLoading.visibility = View.GONE
            lottieLoading.cancelAnimation()
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }

    private fun showUserLoading() {
        binding.apply {
            binding.progressbarAccount.visibility = View.GONE
            lottieLoading.visibility = View.VISIBLE
            lottieLoading.repeatCount = LottieDrawable.INFINITE
            lottieLoading.playAnimation()
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }
}