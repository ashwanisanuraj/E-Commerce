package com.xero.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

        val dao = AppDatabase.getInstance(context).productDao()
        holder.binding.delete.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                dao.deleteProduct(
                    ProductModel(
                        list[position].productId,
                        list[position].productName,
                        list[position].productSp)
                )
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}