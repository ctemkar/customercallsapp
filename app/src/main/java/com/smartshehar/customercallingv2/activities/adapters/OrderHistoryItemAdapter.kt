package com.smartshehar.customercallingv2.activities.adapters

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartshehar.customercallingv2.databinding.ListOrdersHistoryItemBinding

class OrderHistoryItemAdapter(
    val items: List<com.smartshehar.customercallingv2.models.OrderItem>
) :
    RecyclerView.Adapter<OrderHistoryItemAdapter.ViewHolder>() {


    class ViewHolder(val binding: ListOrdersHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListOrdersHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            with(items[position]) {
                tvOrderItemNameHistoryList.text = itemName
                tvOrderItemPriceHistoryList.text = "\u20B9${price}"
                tvQtyHistoryItem.text = "Qty.${quantity}"
                tvOrderItemTotalOrdersHistoryList.text = "Ordered ${totalOrders} times"
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


}