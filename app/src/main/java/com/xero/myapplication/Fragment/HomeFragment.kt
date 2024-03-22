package com.xero.myapplication.Fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.xero.myapplication.R
import com.xero.myapplication.adapter.ProductAdapter
import com.xero.myapplication.databinding.FragmentHomeBinding
import com.xero.myapplication.model.AddProductModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        // Set up the SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter data based on the new query
                adapter.filter.filter(newText)
                return true
            }
        })

        val preference =
            requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)

        if (preference.getBoolean("isCart", false))
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)

        firestore = FirebaseFirestore.getInstance()

        getProduct()
        return binding.root
    }

    private fun getProduct() {
        val list = ArrayList<AddProductModel>()
        firestore.collection("products")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(AddProductModel::class.java)
                    data?.let { product ->
                        list.add(product)
                    }
                }
                adapter = ProductAdapter(requireContext(), list)
                binding.productRV.adapter = adapter
            }
    }
}
