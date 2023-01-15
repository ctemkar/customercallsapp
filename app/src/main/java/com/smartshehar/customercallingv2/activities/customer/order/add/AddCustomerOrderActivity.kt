package com.smartshehar.customercallingv2.activities.customer.order.add

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.activities.adapters.CartItemAdapter
import com.smartshehar.customercallingv2.activities.menuitems.view.ViewMenuItemVM
import com.smartshehar.customercallingv2.databinding.ActivityAddCustomerOrderBinding
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.utils.Constants
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCustomerOrderActivity : AppCompatActivity(), CartItemAdapter.OnItemQuantityChangeListener {

    private val TAG = "AddCustomerOrderActivit"
    lateinit var binding: ActivityAddCustomerOrderBinding
    val viewModel: AddCustomerOrderVM by viewModels()
    val viewMenuItemVM: ViewMenuItemVM by viewModels()
    lateinit var menuItems: ArrayList<MenuItem>
    val customerOrder = CustomerOrder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        if (!intent.hasExtra(Constants.INTENT_DATA_CUSTOMER_ID)) {
            Toast.makeText(this, "Customer data not received", Toast.LENGTH_SHORT).show()
            finish()
        }

        customerOrder.customerId = intent.getLongExtra(Constants.INTENT_DATA_CUSTOMER_ID, 0)

        viewMenuItemVM.getMenuItems().observe(this) {
            when (it.eventStatus) {
                EventStatus.LOADING -> TODO()
                EventStatus.SUCCESS -> {
                    menuItems = it.data as ArrayList<MenuItem>
                    val mAdapter = CartItemAdapter(menuItems)
                    binding.rViewCreateOrderItems.apply {
                        layoutManager = LinearLayoutManager(applicationContext)
                        adapter = mAdapter
                    }
                    mAdapter.setOnItemQuantityChangeListener(this)
                }
                EventStatus.ERROR -> TODO()
                EventStatus.EMPTY -> TODO()
            }
        }

        binding.btPlaceOrder.setOnClickListener {
            viewModel.addCustomerOrder(customerOrder).observe(this) {
                when (it.eventStatus) {
                    EventStatus.LOADING -> {

                    }
                    EventStatus.SUCCESS -> {
                        Toast.makeText(this, "Placed Order", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    EventStatus.ERROR -> {

                    }
                    EventStatus.EMPTY -> {

                    }
                }
            }
        }

    }


    private fun setToolbar() {
        findViewById<TextView>(R.id.tv_tbTitle).text = "Place Order"
        findViewById<ImageButton>(R.id.bt_backToolbar).setOnClickListener {
            finish()
        }
    }


    private var totalAmount: Double = 0.0
    private var totalItems: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onQuantityChange(map: HashMap<Long, Int>) {
        totalAmount = 0.0
        totalItems = 0

        menuItems.forEach {
            if (map.contains(it.itemId)) {
                customerOrder.addOrderItem(it, map[it.itemId]!!)
                totalItems += 1
                totalAmount += (it.price * map[it.itemId]!!)
            }
        }
        customerOrder.orderTotal = totalAmount
        binding.tvTotalItemsCart.text = "Total ($totalItems items)"
        binding.tvTotalAmountCart.text = "${getString(R.string.Rs)}$totalAmount"
    }
}