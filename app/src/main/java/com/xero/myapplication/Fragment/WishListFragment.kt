package com.xero.myapplication.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xero.myapplication.adapter.WishListAdapter
import com.xero.myapplication.databinding.FragmentWishListBinding
import com.xero.myapplication.roomDb.AppDatabase

class WishListFragment : Fragment() {

    private lateinit var binding: FragmentWishListBinding
    private lateinit var list: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWishListBinding.inflate(layoutInflater)

        binding.backToProfile.setOnClickListener {
            // Navigate back to the ProfileFragment
            findNavController().navigateUp()
        }

        val dao = AppDatabase.getInstance(requireContext()).productDao()
        list = ArrayList()


        dao.getAllWishlistItems().observe(viewLifecycleOwner) { wishlistItems ->
            binding.wishList.adapter = WishListAdapter(requireContext(), wishlistItems)

            list.clear()
            for (item in wishlistItems){
                list.add(item.productId)
            }
            updateTotalItems(wishlistItems.size)
        }

        return binding.root
    }

    private fun updateTotalItems(count: Int) {
        binding.totalItem2.text = "Total Wishlisted Item: $count"
    }
}
