package com.xero.myapplication.Fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xero.myapplication.R
import com.xero.myapplication.activity.LoginActivity
import com.xero.myapplication.activity.OrdersActivity
import com.xero.myapplication.activity.UserProfileActivity
import com.xero.myapplication.databinding.FragmentProfileBinding
import com.xero.myapplication.databinding.FragmentWishListBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SharedPreferences and Firestore
        preferences = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        firestore = FirebaseFirestore.getInstance()

        // Load user information
        loadUserInfo(preferences.getString("number", "")!!)

        // Set click listeners for navigation buttons
        binding.getToOrders.setOnClickListener {
            startActivity(Intent(requireContext(), OrdersActivity::class.java))
        }

        binding.goToProfile.setOnClickListener {
            startActivity(Intent(requireContext(), UserProfileActivity::class.java))
        }

        binding.button.setOnClickListener {
            navigateToWishlistFragment()
        }


        // Set click listener for logout button
        binding.logoutBtn.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun loadUserInfo(phoneNumber: String) {
        firestore.collection("users").document(phoneNumber)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userName = document.getString("userName")
                    binding.textView13.text = "Hey, $userName"
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                // For example, display a toast message
                // Toast.makeText(requireContext(), "Failed to load user information", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLogoutConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Logout")
        alertDialogBuilder.setMessage("Are you sure you want to logout?")
        alertDialogBuilder.setPositiveButton("LOGOUT") { dialog, which ->
            // Sign out user
            FirebaseAuth.getInstance().signOut()
            // Redirect to login activity
            requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
        alertDialogBuilder.setNegativeButton("CANCEL") { dialog, which ->
            dialog.dismiss()
        }
        // Create the dialog instance and show it
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(requireContext().getColor(R.color.red))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(requireContext().getColor(R.color.green))
    }

    private fun navigateToWishlistFragment() {
        // Navigate to the wishlist fragment
        findNavController().navigate(R.id.action_profileFragment_to_wishListFragment)
    }
}
