// CategoryActivity.kt
package com.xero.myapplication.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.xero.myapplication.R
import com.xero.myapplication.adapter.CategoryProductAdapter
import com.xero.myapplication.model.AddProductModel

class CategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Retrieve the category and category name passed from the previous activity
        val category = intent.getStringExtra("cate")

        // Set the category name to the TextView
        findViewById<TextView>(R.id.categoryName).text = "Available $category"

        // Fetch products for the specified category
        if (!category.isNullOrEmpty()) {
            getProduct(category)
        } else {
            // Handle the case where category is null or empty
            // You may display an error message or finish the activity
        }
    }

    private fun getProduct(category: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("products")
            .whereEqualTo("productCategory", category)
            .get()
            .addOnSuccessListener { documents ->
                val productList = ArrayList<AddProductModel>()
                for (document in documents) {
                    val product = document.toObject(AddProductModel::class.java)
                    productList.add(product)
                }
                // Bind the fetched products to the RecyclerView
                setupRecyclerView(productList)
            }
            .addOnFailureListener { exception ->
                // Handle errors while fetching data from Firestore
                // You may display an error message or handle the failure accordingly
            }
    }

    private fun setupRecyclerView(productList: ArrayList<AddProductModel>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = CategoryProductAdapter(this, productList)
        recyclerView.adapter = adapter
    }
}
