package com.smartshehar.customercallingv2.activities.home.permissions

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.databinding.ListPermissionsBinding
import com.smartshehar.customercallingv2.models.CustomerOrder

class PermissionsAdapter(val permissionList: ArrayList<PermissionModel>) :
    RecyclerView.Adapter<PermissionsAdapter.ViewHolder>() {

    class ViewHolder(var binding: ListPermissionsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListPermissionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return permissionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            with(permissionList[position]) {
                if (isAllowed) {
                    imgVewPermissionStatus.background =
                        imgVewPermissionStatus.resources.getDrawable(R.drawable.baseline_check_24)
                    btPermissionAllowButton.visibility = View.INVISIBLE
                } else {
                    imgVewPermissionStatus.background =
                        imgVewPermissionStatus.resources.getDrawable(R.drawable.baseline_priority_high_24)
                    btPermissionAllowButton.visibility = View.VISIBLE

                    btPermissionAllowButton.setOnClickListener{
                        listener?.onItemClick(permissionList[position])
                    }
                }
                tvPermissionName.text = permissionName
            }
        }
    }

    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(selectedPermission: PermissionModel)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}