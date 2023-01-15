package com.smartshehar.customercallingv2.activities.adapters

import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartshehar.customercallingv2.databinding.ListOrdersHistoryItemBinding

class OrderHistoryItemAdapter(val historyList: ArrayList<MenuItem>) :
    RecyclerView.Adapter<OrderHistoryItemAdapter.ViewHolder>() {


    class ViewHolder(val binding: ListOrdersHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return historyList.size
    }


}