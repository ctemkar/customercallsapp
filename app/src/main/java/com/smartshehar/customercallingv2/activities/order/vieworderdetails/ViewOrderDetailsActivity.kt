package com.smartshehar.customercallingv2.activities.order.vieworderdetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.activities.adapters.OrderHistoryItemAdapter
import com.smartshehar.customercallingv2.activities.customer.view.ViewCustomerVM
import com.smartshehar.customercallingv2.databinding.ActivityViewOrderDetailsBinding
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.OrderItem
import com.smartshehar.customercallingv2.utils.Constants
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ViewOrderDetailsActivity : AppCompatActivity() {

    private val TAG = "ViewOrderDetailsActivity"
    private lateinit var binding: ActivityViewOrderDetailsBinding
    private val viewModel: ViewOrderDetailsVM by viewModels()
    private val customerViewModel: ViewCustomerVM by viewModels()
    private var customerId: Long = 0
    private var orderId: Long = 0
    private var totalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
        if (!intent.hasExtra(Constants.INTENT_DATA_ORDER_ID) || !intent.hasExtra(Constants.INTENT_DATA_CUSTOMER_ID)) {
            Toast.makeText(this, "Order ID or Customer ID not passed", Toast.LENGTH_SHORT).show()
            finish()
        }
        orderId = intent.getLongExtra(Constants.INTENT_DATA_ORDER_ID, 0)
        customerId = intent.getLongExtra(Constants.INTENT_DATA_CUSTOMER_ID, 0)


        viewModel.getOrderItemsInCustomerOrder(orderId, customerId).observe(this) {
            when (it.eventStatus) {
                EventStatus.LOADING -> TODO()
                EventStatus.SUCCESS -> {
                    if (it.data!!.isEmpty()) {
                        //Load data from API as the data is not available in local
                        //Proceed to load if the server customer ID is present, else throw exception
                        getCustomerOrdersFromServer()
                    } else {
                        loadOrderItemsList(it)
                    }
                }
                EventStatus.ERROR -> TODO()
                EventStatus.EMPTY -> TODO()
                EventStatus.CACHE_DATA -> TODO()
            }
        }


        customerViewModel.getCustomerData(customerId).observe(this) {
            when (it.eventStatus) {
                EventStatus.LOADING -> TODO()
                EventStatus.SUCCESS -> {
                    setCustomerDataIntoView(it)
                }
                EventStatus.ERROR -> TODO()
                EventStatus.EMPTY -> TODO()
                EventStatus.CACHE_DATA -> TODO()
            }
        }


    }

    private fun setCustomerDataIntoView(it: EventData<Customer>) {
        val customer = it.data!!
        binding.tvCustNameOrderDetail.text = customer.firstName
        val addressStringBuilder = StringBuilder()
        addressStringBuilder.append(customer.houseNo).append("\n").append(customer.area)
            .append("\n")
        binding.tvCustAddressOrderDetail.text = addressStringBuilder.toString()
        binding.tvCustContactOrderDetail.text = customer.contactNumber
        setCallButtonListener(customer.contactNumber)
    }

    private fun getCustomerOrdersFromServer() {
        val serverCustomerId: String? =
            intent.getStringExtra(Constants.INTENT_DATA_SERVER_CUSTOMER_ID)
        val serverOrderId: String? = intent.getStringExtra(Constants.INTENT_DATA_SERVER_ORDER_ID)

        if (serverCustomerId == null || serverOrderId == null) {
            Toast.makeText(applicationContext, "Something happened", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.getOrderItemsInCustomerOrderFromServer(
            serverCustomerId = serverCustomerId,
            serverOrderId = serverOrderId
        )
            .observe(this) {
                when (it.eventStatus) {
                    EventStatus.LOADING -> {

                    }
                    EventStatus.SUCCESS -> {
                        Log.d(TAG, "getCustomerOrdersFromServer: ${it.data!!.size}")
                        loadOrderItemsList(it)
                    }
                    EventStatus.ERROR -> {

                    }
                    EventStatus.EMPTY -> TODO()
                    EventStatus.CACHE_DATA -> TODO()
                }
            }


    }

    private fun loadOrderItemsList(it: EventData<List<OrderItem>>) {
        val orderItems = it.data!!
        orderItems.forEach {
            totalAmount += it.quantity * it.price
        }
        val mAdapter = OrderHistoryItemAdapter(orderItems)
        binding.rViewOrderDetailsItems.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = mAdapter
        }
        binding.tvTotalAmountViewOrder.text = "${getString(R.string.Rs)}$totalAmount"
    }

    private fun setCallButtonListener(msPhoneNo: String) {
        binding.btCallCustomer.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + msPhoneNo) //change the number
            startActivity(callIntent)
        }
    }

    private fun setToolbar() {
        findViewById<TextView>(R.id.tv_tbTitle).text = "Order Details"
        findViewById<ImageButton>(R.id.bt_backToolbar).setOnClickListener {
            finish()
        }
    }
}