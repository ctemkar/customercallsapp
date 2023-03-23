package com.smartshehar.customercallingv2.activities.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartshehar.customercallingv2.databinding.ListOrdersHistoryBinding
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.utils.DateFormatter

class OrderHistoryAdapter(private val ordersList: List<CustomerOrder>) :
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ListOrdersHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListOrdersHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {

            root.setOnClickListener {
                listener?.onItemClick(
                    clickedCustomerOrder = ordersList[position]
                )
            }

            with(ordersList[position]) {
                tvOrderTotalOrderList.text = orderTotalAmount.toString()
                tvOrderIdOrderList.text = orderId.toString()
                tvOrderedOnOrderList.text = DateFormatter.getDateFormatted(orderDate)
            }
        }
    }

    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(clickedCustomerOrder: CustomerOrder)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

}