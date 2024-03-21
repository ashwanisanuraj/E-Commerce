package com.xero.myapplication.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.xero.myapplication.R
import com.xero.myapplication.adapter.AllOrderAdapter
import com.xero.myapplication.databinding.ActivityOrdersBinding
import com.xero.myapplication.model.AllOrderModel

class OrdersActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrdersBinding
    private lateinit var adapter : AllOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrdersBinding.inflate(layoutInflater)

        setContentView(binding.root)

        adapter = AllOrderAdapter(ArrayList(), this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        //fetching all orders from firestore
        val preferences = getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        FirebaseFirestore.getInstance().collection("allOrders")
            .whereEqualTo("userId", preferences.getString("number","")!!)
            .get()
            .addOnSuccessListener { documents ->
                val ordersList = ArrayList<AllOrderModel>()
                for (document in documents){
                    val order = document.toObject(AllOrderModel::class.java)
                    ordersList.add(order)
                }
                adapter.setData(ordersList)
            }
    }
}