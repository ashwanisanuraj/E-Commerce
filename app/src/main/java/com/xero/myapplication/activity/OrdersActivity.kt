package com.xero.myapplication.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.xero.myapplication.adapter.AllOrderAdapter
import com.xero.myapplication.databinding.ActivityOrdersBinding
import com.xero.myapplication.model.AllOrderModel

class OrdersActivity : AppCompatActivity(), AllOrderAdapter.OrderStatusChangeListener {

    private lateinit var binding: ActivityOrdersBinding
    private lateinit var adapter: AllOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = AllOrderAdapter(ArrayList(), this)
        adapter.setOrderStatusChangeListener(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Fetching all orders from Firestore
        val preferences = getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        FirebaseFirestore.getInstance().collection("allOrders")
            .whereEqualTo("userId", preferences.getString("number", "")!!)
            .get()
            .addOnSuccessListener { documents ->
                val ordersList = ArrayList<AllOrderModel>()
                for (document in documents) {
                    val order = document.toObject(AllOrderModel::class.java)
                    ordersList.add(order)
                }
                adapter.setData(ordersList)
            }
    }

    override fun onOrderStatusChanged() {
        // Refresh the orders when an order status is changed
        fetchOrders()
    }

    private fun fetchOrders() {
        val preferences = getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        FirebaseFirestore.getInstance().collection("allOrders")
            .whereEqualTo("userId", preferences.getString("number", "")!!)
            .get()
            .addOnSuccessListener { documents ->
                val ordersList = ArrayList<AllOrderModel>()
                for (document in documents) {
                    val order = document.toObject(AllOrderModel::class.java)
                    ordersList.add(order)
                }
                adapter.setData(ordersList)
            }
    }
}
