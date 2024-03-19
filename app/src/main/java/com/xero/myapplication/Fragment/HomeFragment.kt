package com.xero.myapplication.Fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.xero.myapplication.R
import com.xero.myapplication.adapter.CategoryAdapter
import com.xero.myapplication.adapter.ProductAdapter
import com.xero.myapplication.databinding.FragmentHomeBinding
import com.xero.myapplication.model.AddProductModel
import com.xero.myapplication.model.CategoryModel

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        //button2 binding

        val preference = requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)

        if (preference.getBoolean("isCart", false))
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)

        getCategory()
        getProduct()
        getSliderImage()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adjust system UI
        requireActivity().window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun getSliderImage() {
        Firebase.firestore.collection("slider").document("item")
            .get().addOnSuccessListener {
                Glide.with(requireContext()).load(it.get("img")).into(binding.sliderImage)
            }
    }

    private fun getProduct() {
        val list = ArrayList<AddProductModel>()
        Firebase.firestore.collection("products")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents){
                    val data = doc.toObject(AddProductModel::class.java)
                    list.add(data!!)
                }
                binding.productRV.adapter = ProductAdapter(requireContext(), list)
            }
    }

    private fun getCategory() {
        val list = ArrayList<CategoryModel>()
        Firebase.firestore.collection("category")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents){
                    val data = doc.toObject(CategoryModel::class.java)
                    list.add(data!!)
                }
                binding.categoryRV.adapter = CategoryAdapter(requireContext(), list)
            }
    }
}