package com.smartshehar.customercallingv2.activities.customer.order.vieworderdetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.databinding.ActivityViewOrderDetailsBinding
import com.smartshehar.customercallingv2.utils.Constants
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewOrderDetailsActivity : AppCompatActivity() {

    private val TAG = "ViewOrderDetailsActivity"
    private lateinit var binding: ActivityViewOrderDetailsBinding
    private val viewModel: ViewOrderDetailsVM by viewModels()
    private var customerId: Long = 0
    private var orderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!intent.hasExtra(Constants.INTENT_DATA_ORDER_ID) || !intent.hasExtra(Constants.INTENT_DATA_CUSTOMER_ID)) {
            Toast.makeText(this, "Order ID or Customer ID not passed", Toast.LENGTH_SHORT).show()
            finish()
        }
        orderId = intent.getStringExtra(Constants.INTENT_DATA_ORDER_ID).toString()
        customerId = intent.getLongExtra(Constants.INTENT_DATA_CUSTOMER_ID, 0)


        viewModel.getOrderDetails(orderId, customerId).observe(this){
            when(it.eventStatus){
                EventStatus.LOADING -> TODO()
                EventStatus.SUCCESS -> {

                }
                EventStatus.ERROR -> TODO()
                EventStatus.EMPTY -> TODO()
            }
        }


    }
}