package com.xero.myapplication.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.xero.myapplication.R
import com.xero.myapplication.activity.OrdersActivity
import com.xero.myapplication.activity.UserProfileActivity
import com.xero.myapplication.adapter.AllOrderAdapter
import com.xero.myapplication.databinding.FragmentProfileBinding
import com.xero.myapplication.model.AllOrderModel

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentProfileBinding.inflate(layoutInflater)

        binding.getToOrders.setOnClickListener{
            startActivity(Intent(requireContext(), OrdersActivity::class.java))

        }

        binding.goToProfile.setOnClickListener {
            startActivity(Intent(requireContext(), UserProfileActivity::class.java))
        }



        return binding.root

    }

}