package com.xero.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.xero.myapplication.databinding.AllOrderItemLayoutBinding
import com.xero.myapplication.model.AllOrderModel

class AllOrderAdapter(
    private var list: ArrayList<AllOrderModel>,
    private val context: Context
) : RecyclerView.Adapter<AllOrderAdapter.AllOrderViewHolder>() {

    interface OrderStatusChangeListener {
        fun onOrderStatusChanged()
    }

    private var orderStatusChangeListener: OrderStatusChangeListener? = null

    inner class AllOrderViewHolder(val binding: AllOrderItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrderViewHolder {
        val binding =
            AllOrderItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllOrderViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: AllOrderViewHolder, position: Int) {
        val order = list[position]
        holder.binding.productTitle.text = order.name
        holder.binding.productPrice.text = order.price
        holder.binding.productStatus.text = order.status

        if (order.status == "Cancelled") {
            holder.binding.cancelBtn.visibility = View.GONE
        } else {
            holder.binding.cancelBtn.visibility = View.VISIBLE
            holder.binding.cancelBtn.setOnClickListener {
                // Update the UI immediately
                holder.binding.cancelBtn.visibility = View.GONE
                holder.binding.productStatus.text = "Cancelled"

                // Update the status in Firestore
                updateStatus("Cancelled", list[position].orderId!!)
            }
        }

        if (order.status == "Delivered") {
            holder.binding.cancelBtn.visibility = View.GONE
        }
    }

    private fun updateStatus(status: String, orderId: String?) {
        orderId?.let {
            val data = hashMapOf("status" to status)
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("allOrders").document(orderId)
                .update(data as MutableMap<String, Any>)
                .addOnSuccessListener {
                    // Update UI to reflect the status change
                    Toast.makeText(
                        context,
                        "Status Updated, Go back and open orders again for updates to reflect",
                        Toast.LENGTH_SHORT
                    ).show()
                    orderStatusChangeListener?.onOrderStatusChanged()
                }
                .addOnFailureListener { e ->
                    // Handle failure to update status
                    Toast.makeText(
                        context,
                        "Failed to update status: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    fun setData(newList: ArrayList<AllOrderModel>) {
        list = newList
        notifyDataSetChanged()
    }

    fun setOrderStatusChangeListener(listener: OrderStatusChangeListener) {
        orderStatusChangeListener = listener
    }
}
