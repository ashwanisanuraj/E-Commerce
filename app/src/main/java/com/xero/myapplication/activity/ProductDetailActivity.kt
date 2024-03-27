package com.xero.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xero.myapplication.MainActivity
import com.xero.myapplication.R
import com.xero.myapplication.databinding.ActivityProductDetailBinding
import com.xero.myapplication.roomDb.AppDatabase
import com.xero.myapplication.roomDb.ProductDao
import com.xero.myapplication.roomDb.ProductModel
import com.xero.myapplication.roomDb.WishlistItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var productDao: ProductDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productDao = AppDatabase.getInstance(this).productDao()

        val productId = intent.getStringExtra("id")
        if (productId != null) {
            getProductDetails(productId)
        } else {
            Toast.makeText(this, "Invalid Product ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getProductDetails(proId: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("products").document(proId).get()
            .addOnSuccessListener { document ->
                val data = document.data
                if (data != null) {
                    val name = data["productName"] as? String
                    val productDesc = data["productDescription"] as? String
                    val productSp = data["productSp"] as? String
                    val productMrp = data["productMrp"] as? String

                    // Set product name and description
                    binding.textView4.text = name
                    binding.textView6.text = productDesc

                    // Set selling price
                    val sellingPriceText = "Selling Price: ₹$productSp"
                    binding.textView5.text = sellingPriceText

                    // Strike through MRP if available
                    if (!productMrp.isNullOrEmpty()) {
                        val mrpText = " ₹$productMrp"
                        val spannableString = SpannableString(sellingPriceText + mrpText)
                        val startIndex = sellingPriceText.length
                        val endIndex = spannableString.length

                        // Applied strike-through effect
                        spannableString.setSpan(
                            StrikethroughSpan(),
                            startIndex,
                            endIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        // Applied red color to productMrp
                        spannableString.setSpan(
                            ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
                            startIndex,
                            endIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        binding.textView5.text = spannableString
                    }

                    // Set product images
                    val list = data["productImages"] as? ArrayList<String>
                    val slideList =
                        list?.map { SlideModel(it, ScaleTypes.CENTER_INSIDE) } ?: emptyList()
                    binding.imageSlider.setImageList(slideList)

                    // Handle cart and wishlist actions
                    val coverImg = data["productCoverImg"] as? String
                    cartAction(proId, name, productSp, coverImg)
                    wishListAction(proId, name, productSp, coverImg)
                } else {
                    Toast.makeText(this, "Product details not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to fetch product details", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun cartAction(proId: String, name: String?, productSp: String?, coverImg: String?) {
        if (productDao.isProductInCart(proId) != null) {
            binding.textView7.text = "Go to Cart"
            binding.textView7.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
        } else {
            binding.textView7.text = "Add to Cart"
        }
        binding.textView7.setOnClickListener {
            if (productDao.isProductInCart(proId) != null) {
                openCart()
            } else {
                binding.textView7.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                addToCart(proId, name, productSp, coverImg)
                Toast.makeText(this, "Product Added to Cart", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Go to Cart to Remove", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun wishListAction(proId: String, name: String?, productSp: String?, coverImg: String?) {
        if (productDao.isItemInWishlist(proId) != null) {
            binding.wishListBtn.text = "Wishlisted"
        } else {
            binding.wishListBtn.text = "Wishlist"
        }
        binding.wishListBtn.setOnClickListener {
            if (productDao.isItemInWishlist(proId) != null) {
                Toast.makeText(
                    this,
                    "Already in wishlist. go to Profile/Wishlist",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                addToWishlist(proId, name, productSp, coverImg)
                Toast.makeText(this, "Product added to Wish list", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCart() {
        val preference = getSharedPreferences("info", MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", true)
        editor.apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun addToCart(proId: String, name: String?, productSp: String?, coverImg: String?) {
        val data = ProductModel(proId, name, coverImg, productSp)
        GlobalScope.launch(Dispatchers.IO) {
            productDao.insertCartProduct(data)
            binding.textView7.text = "Go to Cart"
        }
    }

    private fun addToWishlist(proId: String, name: String?, productSp: String?, coverImg: String?) {
        val wishlistdata = WishlistItem(proId, name, coverImg, productSp)
        GlobalScope.launch(Dispatchers.IO) {
            productDao.insertWishlistProduct(wishlistdata)
            binding.wishListBtn.text = "Wishlisted"
        }
    }
}


