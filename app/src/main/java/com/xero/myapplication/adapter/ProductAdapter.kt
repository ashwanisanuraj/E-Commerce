package com.xero.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xero.myapplication.activity.ProductDetailActivity
import com.xero.myapplication.databinding.LayoutProductItemBinding
import com.xero.myapplication.model.AddProductModel

class ProductAdapter(val context: Context, val list : ArrayList<AddProductModel>)
    : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){

    inner class ProductViewHolder(val binding: LayoutProductItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = LayoutProductItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val data = list[position]
        Glide.with(context).load(data.productCoverImg).into(holder.binding.imageView)
        holder.binding.textView9.text = data.productName
        holder.binding.textView10.text = data.productCategory
        holder.binding.textView11.text = data.productMrp

        holder.itemView.setOnClickListener{
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", list[position].productId)
            context.startActivity(intent)
        }

        holder.binding.button3.text = data.productSp
    }
}