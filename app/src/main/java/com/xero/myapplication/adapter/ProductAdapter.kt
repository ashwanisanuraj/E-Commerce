package com.xero.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xero.myapplication.activity.ProductDetailActivity
import com.xero.myapplication.databinding.LayoutProductItemBinding
import com.xero.myapplication.model.AddProductModel
import java.util.*
import kotlin.collections.ArrayList

class ProductAdapter(
    private val context: Context,
    private var originalList: ArrayList<AddProductModel>
) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(), Filterable {

    private var filteredList: ArrayList<AddProductModel> = originalList

    // Sort options
    private var sortOption: String? = null

    // Method to set the sort option
    fun setSortOption(option: String) {
        sortOption = option
        notifyDataSetChanged() // Notify adapter of data change
    }

    inner class ProductViewHolder(val binding: LayoutProductItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            LayoutProductItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val data = filteredList[position]
        Glide.with(context).load(data.productCoverImg).into(holder.binding.imageView)
        holder.binding.textView9.text = data.productName
        holder.binding.textView10.text = data.productCategory
        holder.binding.textView11.text = "₹${data.productMrp}"
        holder.binding.textView11.paintFlags =
            holder.binding.textView11.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", filteredList[position].productId)
            context.startActivity(intent)
        }

        holder.binding.button3.text = "₹${data.productSp}"
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint.toString().toLowerCase(Locale.getDefault())
                val filtered = mutableListOf<AddProductModel>()
                for (item in originalList) {
                    if (item.productName.toLowerCase(Locale.getDefault()).contains(query)) {
                        filtered.add(item)
                    }
                }
                val results = FilterResults()
                results.values = filtered
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<AddProductModel>
                notifyDataSetChanged()
            }
        }
    }
}


