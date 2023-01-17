package com.smartshehar.customercallingv2.activities.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartshehar.customercallingv2.databinding.ListMenuItemBinding
import com.smartshehar.customercallingv2.models.MenuItem

class MenuItemAdapter constructor(
    private val menuItemList: List<MenuItem>
) : RecyclerView.Adapter<MenuItemAdapter.ViewHolder>() {


    class ViewHolder(val binding: ListMenuItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListMenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(menuItemList[position]) {
            with(holder.binding) {
                tvMenuItemNameList.text = itemName
                tvMenuItemPriceList.text = "\u20B9${price.toString()}"
                tvMenuItemCategoryList.text = category
            }
        }
    }

    override fun getItemCount(): Int {
        return menuItemList.size
    }


}