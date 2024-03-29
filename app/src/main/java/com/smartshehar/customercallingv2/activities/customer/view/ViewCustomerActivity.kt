package com.smartshehar.customercallingv2.activities.customer.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.activities.adapters.OrderHistoryAdapter
import com.smartshehar.customercallingv2.activities.order.add.AddCustomerOrderActivity
import com.smartshehar.customercallingv2.activities.order.vieworderdetails.ViewOrderDetailsActivity
import com.smartshehar.customercallingv2.databinding.ActivityViewCustomerBinding
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerWithCustomerOrder
import com.smartshehar.customercallingv2.utils.Constants
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text

@AndroidEntryPoint
class ViewCustomerActivity : AppCompatActivity(), OrderHistoryAdapter.OnItemClickListener {

    private val TAG = "ViewCustomerActivity"
    lateinit var binding: ActivityViewCustomerBinding
    private val viewModel: ViewCustomerVM by viewModels()
    var customerId: Long = 0
    private lateinit var customerWithCustomerOrder: CustomerWithCustomerOrder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
        customerId = intent.getLongExtra(Constants.INTENT_DATA_CUSTOMER_ID, 0)
        //Check an ID has been received from calling intent
        Log.d(TAG, "onCreate: $customerId")
        if (customerId == 0L) {
            //Customer ID is not passed return to the calling intent and show error
            Toast.makeText(applicationContext, "Customer ID is not received", Toast.LENGTH_SHORT)
                .show()
            finish()
        }


        binding.btNewCustomerOrder.setOnClickListener {
            val intent = Intent(this, AddCustomerOrderActivity::class.java)
            intent.putExtra(Constants.INTENT_DATA_CUSTOMER_ID, customerId)
            startActivity(intent)
        }
    }


    override fun onResume() {
        super.onResume()
        if (customerId != 0L) {
            loadDataFromVM(customerId)
        }
    }

    private fun loadDataFromVM(customerId: Long) {
        viewModel.getCustomerData(customerId).observe(this) {
            when (it.eventStatus) {
                EventStatus.LOADING -> TODO()
                EventStatus.SUCCESS -> {
                    with(binding) {
                        with(it.data) {
                            tvCustomerViewName.text = this?.firstName ?: ""
                            tvCustomerViewContact.text = this?.contactNumber
                            tvCustomerViewStreet.text = this?.area ?: ""
                            tvCustomerViewFlatNo.text = this?.houseNo

                        }
                    }
                }
                EventStatus.ERROR -> TODO()
                EventStatus.EMPTY -> TODO()
                EventStatus.CACHE_DATA -> TODO()
            }
        }

        viewModel.getCustomerOrders(customerId).observe(this) { it ->
            when (it.eventStatus) {
                EventStatus.LOADING -> TODO()
                EventStatus.SUCCESS -> {
                    showOrdersList(it)
                }
                EventStatus.ERROR -> TODO()
                EventStatus.EMPTY -> {
                    showEmptyLayout()
                }
                EventStatus.CACHE_DATA -> TODO()
            }
        }
    }

    private fun showOrdersList(it: EventData<CustomerWithCustomerOrder>) {
        hideEmptyLayout()
        customerWithCustomerOrder = it.data!!
        val mAdapter = OrderHistoryAdapter(customerWithCustomerOrder.customerOrders)
        binding.rViewRecentOrdersCustomer.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = mAdapter
        }
        mAdapter.setOnItemClickListener(this)
    }

    private fun hideEmptyLayout() {
        binding.rViewRecentOrdersCustomer.visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.ll_emptyLayout).visibility = View.GONE
    }
    private fun showEmptyLayout() {
        binding.rViewRecentOrdersCustomer.visibility = View.GONE
        findViewById<LinearLayout>(R.id.ll_emptyLayout).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_emptyLayoutMessage).text =
            "No orders found\n\nPlace an order below"
    }

    private fun setToolbar() {
        findViewById<TextView>(R.id.tv_tbTitle).text = "Customer Profile"
        findViewById<ImageButton>(R.id.bt_backToolbar).setOnClickListener {
            finish()
        }
    }

    override fun onItemClick(
        clickedCustomerOrder: CustomerOrder
    ) {

        val intent = Intent(this, ViewOrderDetailsActivity::class.java)
        intent.putExtra(Constants.INTENT_DATA_CUSTOMER_ID, customerId)
        intent.putExtra(
            Constants.INTENT_DATA_ORDER_ID,
            clickedCustomerOrder.orderId
        )
        intent.putExtra(Constants.INTENT_DATA_SERVER_ORDER_ID, clickedCustomerOrder.serverOrderId)
        intent.putExtra(
            Constants.INTENT_DATA_SERVER_CUSTOMER_ID,
            clickedCustomerOrder.serverCustomerId
        )
        startActivity(intent)
    }
}