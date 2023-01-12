package com.smartshehar.customercallingv2.activities.menuitems.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.databinding.ActivityAddMenuItemBinding
import com.smartshehar.customercallingv2.events.EventStatus
import com.smartshehar.customercallingv2.models.MenuItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMenuItemActivity : AppCompatActivity() {

    private val TAG = "AddMenuItemActivity"
    lateinit var binding: ActivityAddMenuItemBinding
    val viewModel: AddMenuItemVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenuItemBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btAddMenuItem.setOnClickListener {
            if (validateInput()) {
                val menuItem = MenuItem()
                menuItem.itemName = binding.etAddMenuItemName.text.toString()
                menuItem.description = binding.etAddMenuItemDescription.text.toString()
                menuItem.price = binding.etAddMenuItemPrice.text.toString().toDouble()
                menuItem.category = binding.etAddMenuItemCategory.text.toString()
                //Listen to the status of the added item
                viewModel.addMenuItem(menuItem).observe(this) {
                    when (it.eventStatus) {
                        EventStatus.LOADING -> TODO()
                        EventStatus.SUCCESS -> {
                            finish()
                        }
                        EventStatus.ERROR -> TODO()
                        EventStatus.EMPTY -> TODO()
                    }
                }
            }
        }

        findViewById<ImageButton>(R.id.bt_backToolbar).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.tv_tbTitle).text = "Add Menu Item"

    }

    private fun validateInput(): Boolean {

        return true
    }
}