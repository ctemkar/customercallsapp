package com.smartshehar.customercallingv2.activities.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartshehar.customercallingv2.databinding.ListCustomerOrdersHistoryBinding
import com.smartshehar.customercallingv2.models.CustomerOrder

class OrderHistoryAdapter(private val ordersList: List<CustomerOrder>) :
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ListCustomerOrdersHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListCustomerOrdersHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            with(ordersList[position]) {
                tvOrderTotalOrderList.text = orderTotal.toString()
                tvOrderIdOrderList.text = orderId.substring(0,7)
                tvOrderedOnOrderList.text = orderDate.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

}