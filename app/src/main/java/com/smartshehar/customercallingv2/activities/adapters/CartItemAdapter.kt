package com.smartshehar.customercallingv2.activities.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartshehar.customercallingv2.databinding.ListCartItemBinding
import com.smartshehar.customercallingv2.models.MenuItem

class CartItemAdapter(val menuItems: ArrayList<MenuItem>) :
    RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {

    val cartItemsMap = HashMap<Long, Int>()

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

                cardReduceQuantityCart.setOnClickListener {
                    tvOrderItemQuantityCart.text = reduceQuantityOne(position).toString()
                    if (listener != null) {
                        this@CartItemAdapter.listener!!.onQuantityChange(cartItemsMap)
                    }
                }
                cardAddQuantityCart.setOnClickListener {
                    tvOrderItemQuantityCart.text = addQuantityOne(position).toString()
                    if (listener != null) {
                        this@CartItemAdapter.listener!!.onQuantityChange(cartItemsMap)
                    }
                }

            }
        }
    }

    private fun addQuantityOne(position: Int): Int {
        val key = menuItems[position].itemId
        if (!cartItemsMap.containsKey(key)) {
            //No quantities were present before, so initializing with 1
            cartItemsMap[key] = 1
            return 1
        }
        val quantity = cartItemsMap[key]!!
        cartItemsMap[key] = quantity + 1
        return cartItemsMap[key]!!
    }

    private fun reduceQuantityOne(position: Int): Int {
        val key = menuItems[position].itemId
        if (!cartItemsMap.containsKey(key)) {
            //No quantities were present before, so doing nothing
            return 0
        }
        val quantity: Int = cartItemsMap[key]!!
        if (quantity == 0) {
            cartItemsMap.remove(key)
            return 0
        }
        cartItemsMap[key] = quantity - 1
        return cartItemsMap[key]!!
    }

    var listener: OnItemQuantityChangeListener? = null

    interface OnItemQuantityChangeListener {
        fun onQuantityChange(map: HashMap<Long, Int>)
    }

    fun setOnItemQuantityChangeListener(listener: OnItemQuantityChangeListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }


}