package com.smartshehar.customercallingv2.activities.order.add

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.activities.adapters.CartItemAdapter
import com.smartshehar.customercallingv2.activities.menuitems.view.ViewMenuItemVM
import com.smartshehar.customercallingv2.databinding.ActivityAddCustomerOrderBinding
import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.models.OrderItem
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerOrderWithOrderItem
import com.smartshehar.customercallingv2.utils.Constants
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.log

@AndroidEntryPoint
class AddCustomerOrderActivity : AppCompatActivity(), CartItemAdapter.OnItemQuantityChangeListener {

    private val TAG = "AddCustomerOrderActivit"
    lateinit var binding: ActivityAddCustomerOrderBinding
    val viewModel: AddCustomerOrderVM by viewModels()
    private val viewMenuItemVM: ViewMenuItemVM by viewModels()
    lateinit var menuItems: ArrayList<MenuItem>
    lateinit var orderItems: ArrayList<OrderItem>
    private var customerId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        if (!intent.hasExtra(Constants.INTENT_DATA_CUSTOMER_ID)) {
            Toast.makeText(this, "Customer data not received", Toast.LENGTH_SHORT).show()
            finish()
        }

        customerId = intent.getLongExtra(Constants.INTENT_DATA_CUSTOMER_ID, 0)
        viewMenuItemVM.getMenuItems().observe(this) {
            when (it.eventStatus) {
                EventStatus.LOADING -> {
                    if(it.data !=null){
                        initializeCustomerOrderWithZeroQuantity(it.data!!)
                        setMenuItemsToCartList(it)
                    }
                }
                EventStatus.SUCCESS -> {
                    initializeCustomerOrderWithZeroQuantity(it.data!!)
                    setMenuItemsToCartList(it)
                }
                EventStatus.ERROR -> TODO()
                EventStatus.EMPTY -> TODO()
                EventStatus.CACHE_DATA -> TODO()
            }
        }

        binding.btPlaceOrder.setOnClickListener {
            placeCustomerOrder()
        }

    }

    private fun initializeCustomerOrderWithZeroQuantity(data: List<MenuItem>) {
        orderItems = java.util.ArrayList()
        data.forEach {
            val orderItem = OrderItem()
            orderItem.apply {
                itemName = it.itemName
                price = it.price
                category = it.category
                menuItemId = it._id
                localMenuId = it.itemId
            }
            orderItems.add(orderItem)
        }
        lifecycleScope.launch{
            orderItems = viewModel.getOrderedCounts(orderItems,customerId) as ArrayList<OrderItem>
        }
        orderItems
    }

    private fun placeCustomerOrder() {
        if (totalAmount == 0.toDouble()) {
            Toast.makeText(this, "No Items to order", Toast.LENGTH_SHORT).show()
            return
        }
//        orderItems.forEach {
//            if (it.quantity == 0) {
//                orderItems.remove(it)
//            }
//            Log.d(TAG, "placeCustomerOrder: ${it.quantity} ${it.itemName}")
//        }

        viewModel.addCustomerOrder(customerId, orderItems).observe(this) {
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
                EventStatus.CACHE_DATA -> TODO()
            }
        }
    }

    private fun setMenuItemsToCartList(it: EventData<List<MenuItem>>) {
        menuItems = it.data as ArrayList<MenuItem>
        val mAdapter = CartItemAdapter(menuItems, orderItems)
        binding.rViewCreateOrderItems.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = mAdapter
        }
        mAdapter.setOnItemQuantityChangeListener(this)
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
    override fun onQuantityChange(position: Int, updatedQuantity: Int) {
        totalAmount = 0.0
        totalItems = 0

        orderItems[position].quantity = updatedQuantity
        orderItems.forEach {
            totalAmount += it.quantity * it.price
        }

        binding.tvTotalItemsCart.text = "Total ($totalItems items)"
        binding.tvTotalAmountCart.text = "${getString(R.string.Rs)}$totalAmount"
    }
}