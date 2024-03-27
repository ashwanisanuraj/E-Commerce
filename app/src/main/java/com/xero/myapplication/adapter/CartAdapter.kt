package com.xero.myapplication.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xero.myapplication.R
import com.xero.myapplication.activity.ProductDetailActivity
import com.xero.myapplication.databinding.LayoutCartItemBinding
import com.xero.myapplication.roomDb.AppDatabase
import com.xero.myapplication.roomDb.ProductModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartAdapter(private val context: Context, private val list: List<ProductModel>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: LayoutCartItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val dao by lazy { AppDatabase.getInstance(context).productDao() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = LayoutCartItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = list[position]
        with(holder.binding) {
            Glide.with(context)
                .load(currentItem.productImage)
                .error(R.drawable.logo) // Placeholder image in case of loading error
                .into(imageView3)
            textView8.text = currentItem.productName
            textView12.text = "₹${currentItem.productSp}"

            // Handle click event to open product detail activity
            root.setOnClickListener {
                val intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra("id", currentItem.productId)
                context.startActivity(intent)
            }

            // Handle click event to delete item from the cart
            delete.setOnClickListener {
                showDeleteConfirmationDialog(currentItem)
            }
        }
    }

    private fun showDeleteConfirmationDialog(item: ProductModel) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Confirm Delete")
        alertDialogBuilder.setMessage("Are you sure you want to delete this item?")
        alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                dao.deleteCartProduct(item)
            }
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
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

    override fun getItemCount(): Int = list.size
}
