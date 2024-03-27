package com.xero.myapplication.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xero.myapplication.R
import com.xero.myapplication.activity.ProductDetailActivity
import com.xero.myapplication.databinding.LayoutWishlistItemBinding
import com.xero.myapplication.roomDb.AppDatabase
import com.xero.myapplication.roomDb.ProductModel
import com.xero.myapplication.roomDb.WishlistItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WishListAdapter(private val context: Context, private var list: List<WishlistItem>) :
    RecyclerView.Adapter<WishListAdapter.WishlistViewHolder>() {

    inner class WishlistViewHolder(val binding: LayoutWishlistItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val dao by lazy { AppDatabase.getInstance(context).productDao() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val binding =
            LayoutWishlistItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return WishlistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val currentItem = list[position]
        with(holder.binding) {
            Glide.with(context).load(currentItem.productImage).into(imageViewWL)
            tvWL.text = currentItem.productName

            // Handle click event to open product detail activity
            root.setOnClickListener {
                val intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra("id", currentItem.productId)
                context.startActivity(intent)
            }

            // Handle click event to remove item from the wishlist
            deleteWL.setOnClickListener {
                showDeleteConfirmationDialog(currentItem)
            }

            // Check if the product is already in the cart and hide the button if it is
            CoroutineScope(Dispatchers.IO).launch {
                val productDao = AppDatabase.getInstance(context).productDao()
                val product = productDao.isProductInCart(currentItem.productId)
                addToCartButton.visibility = if (product != null) View.GONE else View.VISIBLE
            }

            // Handle click event to add item to cart
            addToCartButton.setOnClickListener {
                addToCartButton.visibility = View.GONE
                Toast.makeText(context, "Product Added to Cart", Toast.LENGTH_SHORT).show()
                addItemToCart(currentItem)
            }
        }
    }


    private fun showDeleteConfirmationDialog(item: WishlistItem) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Confirm Delete")
        alertDialogBuilder.setMessage("Are you sure you want to delete this item?")
        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
            CoroutineScope(Dispatchers.IO).launch {
                dao.deleteWishlistProduct(item)
            }
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        // Show the dialog only once after configuring it
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(context, R.color.red))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(context, R.color.green))
    }

    private fun addItemToCart(item: WishlistItem) {
        CoroutineScope(Dispatchers.IO).launch {
            val productDao = AppDatabase.getInstance(context).productDao()
            val product = ProductModel(
                item.productId,
                item.productName,
                item.productImage,
                item.productSp,

            )
            productDao.insertCartProduct(product)
        }
    }


    override fun getItemCount(): Int = list.size
}

