package com.smartshehar.customercallingv2.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartshehar.customercallingv2.databinding.ListAvailableRestaurantsBinding
import com.smartshehar.customercallingv2.models.Restaurant

class AvailableRestaurantAdapter(val restaurantList: List<Restaurant>) :
    RecyclerView.Adapter<AvailableRestaurantAdapter.ViewHolder>() {

    class ViewHolder(val binding: ListAvailableRestaurantsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = ListAvailableRestaurantsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            root.setOnClickListener {
                listener?.onClick(position)
            }
            with(restaurantList[position]) {
//                if(position == selectedPosition){
//                    imgViewSelectedAvailableList.visibility = View.VISIBLE
//                }else{
//                    imgViewSelectedAvailableList.visibility = View.INVISIBLE
//                }
                tvRestaurantNameAvailableList.text = restaurantName
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

}