package com.xero.myapplication.Fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
    private var productList: ArrayList<AddProductModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize adapter
        adapter = ProductAdapter(requireContext(), productList)
        binding.productRV.adapter = adapter

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

        // Set up the spinner for sorting options
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.sortSpinner.adapter = adapter
        }

        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                sortProducts(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle when nothing is selected in the spinner
            }
        }

        val preference =
            requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)

        if (preference.getBoolean("isCart", false))
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)

        firestore = FirebaseFirestore.getInstance()

        getProduct()
    }

    private fun getProduct() {
        firestore.collection("products")
            .get().addOnSuccessListener {
                productList.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(AddProductModel::class.java)
                    data?.let { product ->
                        productList.add(product)
                    }
                }
                adapter.notifyDataSetChanged() // Notify adapter of data change
            }
    }

    private fun sortProducts(sortOption: String) {
        when (sortOption) {
            "Random" -> productList.shuffle()
            "Low to High" -> productList.sortBy { it.productSp }
            "High to Low" -> productList.sortByDescending { it.productSp }
            // Add more sorting options as needed
        }
        adapter.notifyDataSetChanged()
    }
}
