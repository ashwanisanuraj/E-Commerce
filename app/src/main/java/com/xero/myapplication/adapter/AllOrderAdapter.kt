package com.xero.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.xero.myapplication.databinding.AllOrderItemLayoutBinding
import com.xero.myapplication.model.AllOrderModel


class AllOrderAdapter (var list : ArrayList<AllOrderModel>, val context: Context)
    : RecyclerView.Adapter<AllOrderAdapter.AllOrderViewHolder>(){

    inner class AllOrderViewHolder (val binding : AllOrderItemLayoutBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrderViewHolder {
        return AllOrderViewHolder(
            AllOrderItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AllOrderViewHolder, position: Int) {
        holder.binding.productTitle.text = list[position].name
        holder.binding.productPrice.text = list[position].price


        when (list[position].status) {
            "Ordered" -> {
                holder.binding.productStatus.text = "Ordered"
            }

            "Proceed" -> {
                holder.binding.productStatus.text = "Proceeded"
            }

            "Delivered" -> {
                holder.binding.productStatus.text = "Delivered"
            }

            "Cancelled" -> {
                holder.binding.productStatus.text = "Cancelled"
            }
        }
    }

    fun setData(newList: ArrayList<AllOrderModel>) {
        list = newList
        notifyDataSetChanged()
    }

}