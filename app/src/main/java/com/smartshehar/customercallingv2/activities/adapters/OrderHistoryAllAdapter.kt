package com.smartshehar.customercallingv2.activities.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartshehar.customercallingv2.databinding.ListOrdersHistoryAllBinding
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerOrderWithCustomer
import com.smartshehar.customercallingv2.utils.DateFormatter

class OrderHistoryAllAdapter(private val ordersList: List<CustomerOrderWithCustomer>) :
    RecyclerView.Adapter<OrderHistoryAllAdapter.ViewHolder>() {

    class ViewHolder(val binding: ListOrdersHistoryAllBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListOrdersHistoryAllBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {

            root.setOnClickListener {
                listener?.onItemClick(position, ordersList[position].customerOrder.orderId.toLong())
            }

            with(ordersList[position].customerOrder) {
                tvTotalAmountOrderListAll.text = orderTotalAmount.toString()
                tvCustomerContactOrderListAll.text = ordersList[position].customer.contactNumber.toString()
                tvOrderedOnOrderListAll.text = DateFormatter.getDateFormatted(orderDate)
                tvCustomerNameOrderListAll.text = ordersList[position].customer.firstName
            }
        }
    }

    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int, orderId: Long)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

}