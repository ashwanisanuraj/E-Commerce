package com.xero.myapplication.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.xero.myapplication.R
import com.xero.myapplication.activity.AddressActivity
import com.xero.myapplication.activity.CategoryActivity
import com.xero.myapplication.adapter.cartAdapter
import com.xero.myapplication.databinding.ActivityProductDetailBinding
import com.xero.myapplication.databinding.FragmentCartBinding
import com.xero.myapplication.roomDb.AppDatabase
import com.xero.myapplication.roomDb.ProductModel

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(layoutInflater)

        val preference = requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", false)
        editor.apply()

        val dao = AppDatabase.getInstance(requireContext()).productDao()

        dao.getAllProduct().observe(requireActivity()){
            binding.cartRv.adapter = cartAdapter(requireContext(), it)


            totalCost(it)

        }

        return binding.root
    }

    private fun totalCost(data: List<ProductModel>?) {

        var total = 0
        for (item in data!!){
            val priceString = item.productSp?.replace(",", "") // Remove the comma
            val price = priceString?.toIntOrNull() ?: 0 // Convert string to integer, handle null or invalid cases
            total += price
        }

        binding.totalItem.text = "TOTAL ITEM: ${data.size}"
        binding.totalCost.text = "TOTAL COST: $total"

        binding.checkOut.setOnClickListener{
            //whenever we click on this new activity will open. for that we need to go to categoryadapter.kt
            val intent = Intent(context, AddressActivity::class.java)
            intent.putExtra("totalCost", total)
            startActivity(intent)
        }

    }
}