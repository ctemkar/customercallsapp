package com.smartshehar.customercallingv2.activities.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartshehar.customercallingv2.databinding.ListCustomerHomeBinding
import com.smartshehar.customercallingv2.models.Customer

class CustomerListHomeAdapter(var customerList: List<Customer>) :
    RecyclerView.Adapter<CustomerListHomeAdapter.ViewHolder>() {

    var listener: OnItemClickListener? = null

    class ViewHolder(val binding: ListCustomerHomeBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateData(customerList: List<Customer>){
        this.customerList = customerList
        notifyDataSetChanged()
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListCustomerHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(customerList[position]) {
            with(holder.binding) {
                root.setOnClickListener {
                    if (position != RecyclerView.NO_POSITION) {
                        if (listener != null) {
                            listener?.onItemClick(position)
                        }
                    }
                }
                tvCustomerNameHomeList.text = firstName
                tvCustomerPhoneHomeList.text = contactNumber
            }
        }
    }

    fun setOnClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return customerList.size
    }


}