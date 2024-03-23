package com.xero.myapplication.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xero.myapplication.activity.AddressActivity
import com.xero.myapplication.adapter.cartAdapter
import com.xero.myapplication.databinding.FragmentCartBinding
import com.xero.myapplication.roomDb.AppDatabase
import com.xero.myapplication.roomDb.ProductModel

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var list: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(layoutInflater)


        val preference =
            requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", false)
        editor.apply()

        val dao = AppDatabase.getInstance(requireContext()).productDao()
        list = ArrayList()

        dao.getAllProduct().observe(requireActivity()) {
            binding.cartRv.adapter = cartAdapter(requireContext(), it)

            list.clear()
            for (data in it) {
                list.add(data.productId)
            }

            totalCost(it)

        }



        return binding.root
    }


    private fun totalCost(data: List<ProductModel>?) {


        var total = 0
        for (item in data!!) {
            val priceString = item.productSp?.replace(",", "") // Remove the comma
            val price = priceString?.toIntOrNull()
                ?: 0 // Convert string to integer, handle null or invalid cases
            total += price
        }

        binding.totalItem.text = "Total Item: ${data.size}"
        binding.totalCost.text = "Total Cost: ₹$total"

        binding.checkOut.setOnClickListener {
            if (isEmpty()) {
                Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(context, AddressActivity::class.java)

                val b = Bundle()
                b.putStringArrayList("productIds", list)
                b.putString("totalCost", total.toString())
                intent.putExtras(b)
                startActivity(intent)
            }
        }
    }

    // Enable the checkout button if there are products in the cart
    private fun isEmpty(): Boolean {
        return list.isEmpty()

    }
}