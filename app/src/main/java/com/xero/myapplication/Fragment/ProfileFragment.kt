package com.xero.myapplication.Fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xero.myapplication.activity.LoginActivity
import com.xero.myapplication.activity.OrdersActivity
import com.xero.myapplication.activity.UserProfileActivity
import com.xero.myapplication.databinding.FragmentProfileBinding

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

        // Set click listener for logout button
        binding.logoutBtn.setOnClickListener {
            // Sign out user
            FirebaseAuth.getInstance().signOut()
            // Redirect to login activity
            requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
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
}
