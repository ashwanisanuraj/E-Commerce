package com.xero.myapplication.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xero.myapplication.R
import com.xero.myapplication.activity.ProductDetailActivity
import com.xero.myapplication.databinding.LayoutCartItemBinding
import com.xero.myapplication.roomDb.AppDatabase
import com.xero.myapplication.roomDb.ProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class cartAdapter(val context: Context, val list: List<ProductModel>) :
    RecyclerView.Adapter<cartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: LayoutCartItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Define dao variable as a property of the adapter class
    private val dao = AppDatabase.getInstance(context).productDao()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = LayoutCartItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        Glide.with(context).load(list[position].productImage).into(holder.binding.imageView3)
        holder.binding.textView8.text = list[position].productName
        holder.binding.textView12.text = list[position].productSp

        //to make item in cart clickable
        holder.itemView.setOnClickListener{
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", list[position].productId)
            context.startActivity(intent)
        }

        holder.binding.delete.setOnClickListener {
            showDeleteConfirmationDialog(position)
        }
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(context, R.style.AlertDialogCustomStyle)
        alertDialogBuilder.setTitle("Confirm Delete")
        alertDialogBuilder.setMessage("Are you sure you want to delete this item?")
        alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
            GlobalScope.launch(Dispatchers.IO) {
                dao.deleteProduct(
                    ProductModel(
                        list[position].productId,
                        list[position].productName,
                        list[position].productSp)
                )
            }
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        // Show the dialog only once after configuring it
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(context, R.color.red))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(context, R.color.green))
    }


    override fun getItemCount(): Int {
        return list.size
    }
}
