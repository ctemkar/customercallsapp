package com.smartshehar.customercallingv2.activities.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartshehar.customercallingv2.databinding.ListCartItemBinding
import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.models.OrderItem

class CartItemAdapter(
    val menuItems: ArrayList<MenuItem>,
    val orderItem: ArrayList<OrderItem>
) : RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {


    class ViewHolder(val binding: ListCartItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            with(menuItems[position]) {
                tvOrderItemNameCartList.text = itemName
                tvOrderItemPriceCartList.text = "\u20B9 ${price}"
                tvOrderItemTotalOrdersCartList.text= "Ordered ${orderItem[position].totalOrders} times"
                cardReduceQuantityCart.setOnClickListener {
                    val updatedQuantity = reduceQuantityOne(position)
                    tvOrderItemQuantityCart.text = updatedQuantity.toString()
                    notifyClickListener(position, updatedQuantity)
                }
                cardAddQuantityCart.setOnClickListener {
                    val updatedQuantity = addQuantityOne(position)
                    tvOrderItemQuantityCart.text = updatedQuantity.toString()
                    notifyClickListener(position, updatedQuantity)
                }

            }
        }
    }

    private fun notifyClickListener(position: Int, updatedQuantity: Int) {
        if (listener != null) {
            this.listener!!.onQuantityChange(position, updatedQuantity)
        }
    }

    private fun addQuantityOne(position: Int): Int {
        orderItem[position].quantity += 1
        return orderItem[position].quantity
    }

    private fun reduceQuantityOne(position: Int): Int {
        if (orderItem[position].quantity == 0) {
            return 0
        }
        orderItem[position].quantity -= 1
        return orderItem[position].quantity
    }

    var listener: OnItemQuantityChangeListener? = null

    interface OnItemQuantityChangeListener {
        fun onQuantityChange(position: Int, updatedQuantity: Int)
    }

    fun setOnItemQuantityChangeListener(listener: OnItemQuantityChangeListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }


}