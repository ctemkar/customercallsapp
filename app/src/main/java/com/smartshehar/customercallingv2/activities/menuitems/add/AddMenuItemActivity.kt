package com.smartshehar.customercallingv2.activities.menuitems.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.databinding.ActivityAddMenuItemBinding
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMenuItemActivity : AppCompatActivity() {

    private val TAG = "AddMenuItemActivity"
    lateinit var binding: ActivityAddMenuItemBinding
    private val viewModel: AddMenuItemVM by viewModels()
    private var menuItem = MenuItem()
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenuItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.getBooleanExtra(Constants.INTENT_IS_EDIT_MODE, false)) {
            isEditMode = true
            setInitialFieldValues()
        }

        binding.btAddMenuItem.setOnClickListener {
            if (validateInput()) {
                menuItem.itemName = binding.etAddMenuItemName.text.toString()
                menuItem.description = binding.etAddMenuItemDescription.text.toString()
                menuItem.price = binding.etAddMenuItemPrice.text.toString().toDouble()
                menuItem.category = binding.etAddMenuItemCategory.text.toString()
                //Listen to the status of the added item
                if (isEditMode) {
                    viewModel.updateMenuItem(menuItem).observe(this) {
                        handleMenuItemResult(it)
                    }
                } else {
                    viewModel.addMenuItem(menuItem).observe(this) {
                        handleMenuItemResult(it)
                    }
                }
            }
        }

        findViewById<ImageButton>(R.id.bt_backToolbar).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.tv_tbTitle).text =
            if (isEditMode) "Update Item" else "Add Menu Item"

    }

    private fun handleMenuItemResult(it: EventData<MenuItem>) {
        when (it.eventStatus) {
            EventStatus.LOADING -> TODO()
            EventStatus.SUCCESS -> {
                finish()
            }
            EventStatus.ERROR -> TODO()
            EventStatus.EMPTY -> TODO()
            EventStatus.CACHE_DATA -> TODO()
        }
    }

    private fun setInitialFieldValues() {
        menuItem = intent.getSerializableExtra(Constants.INTENT_DATA_MENU_ITEM) as MenuItem
        binding.etAddMenuItemName.setText(menuItem.itemName)
        binding.etAddMenuItemDescription.setText(menuItem.description)
        binding.etAddMenuItemPrice.setText(menuItem.price.toString())
        binding.etAddMenuItemCategory.setText(menuItem.category)
        binding.btAddMenuItem.text = "Update"
    }

    private fun validateInput(): Boolean {

        return true
    }
}