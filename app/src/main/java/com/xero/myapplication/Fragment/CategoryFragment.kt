package com.xero.myapplication.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.xero.myapplication.adapter.CategoryAdapter
import com.xero.myapplication.databinding.FragmentCategoryBinding
import com.xero.myapplication.model.CategoryModel

class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCategoryBinding.inflate(layoutInflater)
        getCategory()
        return binding.root
    }

    private fun getCategory() {
        val list = ArrayList<CategoryModel>()
        val db = FirebaseFirestore.getInstance()
        db.collection("category")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val category = document.toObject(CategoryModel::class.java)
                    list.add(category)
                }
                // Set up RecyclerView
                binding.categoryRV.layoutManager = LinearLayoutManager(requireContext())
                binding.categoryRV.adapter = CategoryAdapter(requireContext(), list)
            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }
    }



}