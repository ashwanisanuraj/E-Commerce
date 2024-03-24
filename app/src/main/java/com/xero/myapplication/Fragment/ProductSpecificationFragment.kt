package com.xero.myapplication.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.xero.myapplication.databinding.FragmentProductSpecificationBinding

class ProductSpecificationFragment : Fragment() {

    private lateinit var binding: FragmentProductSpecificationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductSpecificationBinding.inflate(inflater, container, false)

        val productId = arguments?.getString("productId")

        if (productId != null) {
            getProductDetails(productId)
        } else {
            // Handle case where productId is null
            binding.specificationTv.text = "Error: Product ID missing"
        }

        return binding.root
    }

    private fun getProductDetails(productId: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                val productSpecification = document.getString("specification") // Assuming this field exists
                binding.specificationTv.text = productSpecification ?: "No specification available"
            }
            .addOnFailureListener { exception ->
                // Handle failure scenario (log error, display user-friendly message)
                Log.e("ProductDetailsFragment", "Error fetching product details:", exception)
                binding.specificationTv.text = "Failed to retrieve product details"
            }
    }
}
