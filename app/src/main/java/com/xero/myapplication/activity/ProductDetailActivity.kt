package com.xero.myapplication.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.xero.myapplication.MainActivity
import com.xero.myapplication.databinding.ActivityProductDetailBinding
import com.xero.myapplication.roomDb.AppDatabase
import com.xero.myapplication.roomDb.ProductDao
import com.xero.myapplication.roomDb.ProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)

        getProductDetails(intent.getStringExtra("id"))

        setContentView(binding.root)
    }

    private fun getProductDetails(proId: String?) {
        Firebase.firestore.collection("products")
            .document(proId!!).get().addOnSuccessListener {
                val list = it.get("productImages") as ArrayList<String>

                val name = it.getString("productName")
                val productSp = it.getString("productSp")
                val productDesc = it.getString("productDescription")

                binding.textView4.text = name
                binding.textView5.text = productSp
                binding.textView6.text = productDesc

                    val slideList = ArrayList<SlideModel>()
                    for (data in list) {
                        slideList.add(SlideModel(data, ScaleTypes.CENTER_CROP))
                    }


                    cartAction(proId, name, productSp, it.getString("productCoverImg"))

                    // Set the slide list to the imageSlider
                    binding.imageSlider.setImageList(slideList)


            }.addOnFailureListener { exception ->
                // Log the error message for debugging
                Log.e(TAG, "Error fetching product details: ", exception)
                Toast.makeText(this, "Failed to fetch product details", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cartAction(proId: String, name: String?, productSp: String?, coverImg: String?) {

        val productDao = AppDatabase.getInstance(this).productDao()
        if (productDao.isExit(proId) != null){
            binding.textView7.text = "Go to Cart"
        }else{
            binding.textView7.text = "Add to Cart"
        }

        binding.textView7.setOnClickListener{
            if (productDao.isExit(proId) != null){
                openCart()
            }else{
                addToCart(productDao, proId, name, productSp, coverImg)
            }
        }

    }

    private fun addToCart(productDao: ProductDao, proId: String, name: String?, productSp: String?, coverImg: String?) {
        val data = ProductModel(proId, name, coverImg, productSp)
        lifecycleScope.launch(Dispatchers.IO) {
            productDao.insertProduct(data)
            binding.textView7.text = "Go to Cart"

        }

    }

    private fun openCart() {
        val preference = this.getSharedPreferences("info", MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", true)
        editor.apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}