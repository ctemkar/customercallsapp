package com.smartshehar.customercallingv2.activities.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartshehar.customercallingv2.databinding.ListCustomerHomeBinding
import com.smartshehar.customercallingv2.models.Customer

class CustomerListHomeAdapter(var customerList: List<Customer>) :
    RecyclerView.Adapter<CustomerListHomeAdapter.ViewHolder>() {


    class ViewHolder(val binding: ListCustomerHomeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListCustomerHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(customerList[position]) {
            with(holder.binding) {
                tvCustomerNameHomeList.text = firstName
                tvCustomerPhoneHomeList.text = msPhoneNo
            }
        }
    }

    override fun getItemCount(): Int {
        return customerList.size
    }


}