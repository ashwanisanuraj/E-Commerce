package com.xero.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.xero.myapplication.MainActivity
import org.json.JSONObject
import com.xero.myapplication.R
import com.xero.myapplication.roomDb.AppDatabase
import com.xero.myapplication.roomDb.ProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity(), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)


        Checkout.preload(applicationContext)
        val co = Checkout()
        // apart from setting it in AndroidManifest.xml, keyId can also be set
        // programmatically during runtime
        co.setKeyID("rzp_test_pAd8uaMSLOnIbq")

        val price = intent.getStringExtra("totalCost")
        try {
            val options = JSONObject()
            options.put("name", "Ashwani Raj's E-Commerce App")
            options.put("description", "Best ECOMMERCE App...")
            //You can omit the image option to fetch the image from the dashboard
            options.put(
                "image",
                "https://firebasestorage.googleapis.com/v0/b/e-commerce-9dc89.appspot.com/o/slider%2F4d617b66-a5f4-4b07-a677-60698f0cb7dd.jpg?alt=media&token=eaedb000-9c1b-41d5-b4df-1b3c744b8929"
            )
            options.put("theme.color", "#1F51FF");
            options.put("currency", "INR");
            options.put("amount", (price!!.toInt()*100))//pass amount in currency subunits
            options.put("email", "ashwanisanuraj@gmail.com")
            options.put("contact", "8863803261")

            co.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()
        uploadData()
    }

    private fun uploadData() {
        val id = intent.getStringArrayListExtra("productIds")
        for (currentId in id!!) {
            fetchData(currentId)
        }
    }

    private fun fetchData(productId: String?) {

        val dao = AppDatabase.getInstance(this).productDao()

        Firebase.firestore.collection("products").document(productId!!).get().addOnSuccessListener {
            lifecycleScope.launch(Dispatchers.IO) {
                dao.deleteCartProduct(ProductModel(productId))
            }
            saveData(it.getString("productName"),
                it.getString("productSp"),
                productId)
        }

    }

    private fun saveData(name: String?, price: String?, productId: String) {
        val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        val data = hashMapOf<String, Any>()
        data["name"] = name!!
        data["price"] = price!!
        data["productId"] = productId
        data["status"] = "Ordered"
        data["userId"] = preferences.getString("number", "")!!

        val firestore = Firebase.firestore.collection("allOrders")
        val key = firestore.document().id
        data["orderId"] = key

        firestore.document(key).set(data).addOnSuccessListener {
            Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
            .addOnFailureListener {
                Toast.makeText(this, "Somthing went wrong", Toast.LENGTH_SHORT).show()
            }

    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
    }
}