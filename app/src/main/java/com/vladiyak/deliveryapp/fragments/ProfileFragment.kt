package com.vladiyak.deliveryapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.StartActivity
import com.vladiyak.deliveryapp.databinding.FragmentProfileBinding
import com.vladiyak.deliveryapp.utils.Resource
import com.vladiyak.deliveryapp.utils.hideKeyboard
import com.vladiyak.deliveryapp.utils.showBottomNavigationView
import com.vladiyak.deliveryapp.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onHomeClick()

        onLogoutClick()

        binding.shareLayout.setOnClickListener {
            showShareDialog()
        }

        binding.needHelpLayout.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_productAdderFragment)
        }

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }

        binding.linearAbout.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_aboutFragment)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Glide.with(requireView()).load(it.data!!.imagePath)
                            .placeholder(R.drawable.loading_animation)
                            .error(R.drawable.ic_broken_image)
                            .into(binding.imageUser)
                        binding.tvUserName.text = "${it.data.fullName}"
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.progressbarSettings.visibility = View.GONE
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun onLogoutClick() {
        binding.linearLogOut.setOnClickListener {
            showLogoutAlertDialog()
        }
    }

    private fun showLogoutAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.logout_alert_dialog, null, false)
        alertDialogBuilder.setView(view)

        val alertDialog = alertDialogBuilder.create()

        val btnNo = view.findViewById<Button>(R.id.btn_no)
        val btnYes = view.findViewById<Button>(R.id.btn_yes)

        btnNo.setOnClickListener {
            alertDialog.dismiss()
        }

        btnYes.setOnClickListener {
            alertDialog.dismiss()

            FirebaseAuth.getInstance().signOut()

            val intent = Intent(context, StartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            requireActivity().finish()
        }

        alertDialog.show()
    }

    private fun showShareDialog() {
        val appPackageName = requireContext().packageName
        val playStoreUrl = "https://play.google.com/store/apps/details?id=$appPackageName"

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this cool app!")
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Download the app from:\n$playStoreUrl")

        val chooserIntent = Intent.createChooser(shareIntent, "Share via")

        if (shareIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(chooserIntent)
        } else {
            Toast.makeText(
                requireContext(),
                "No app available to handle this action",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun onHomeClick() {
        val btm = activity?.findViewById<BottomNavigationView>(R.id.bottomNavMenu)
        btm?.menu?.getItem(0)?.setOnMenuItemClickListener {
            activity?.onBackPressed()
            true
        }
        hideKeyboard()
    }



    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}