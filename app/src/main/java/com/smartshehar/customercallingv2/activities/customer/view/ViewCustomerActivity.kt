package com.smartshehar.customercallingv2.activities.customer.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.activities.adapters.OrderHistoryAdapter
import com.smartshehar.customercallingv2.activities.customer.order.add.AddCustomerOrderActivity
import com.smartshehar.customercallingv2.activities.customer.order.vieworderdetails.ViewOrderDetailsActivity
import com.smartshehar.customercallingv2.databinding.ActivityViewCustomerBinding
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerWithCustomerOrder
import com.smartshehar.customercallingv2.utils.Constants
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ViewCustomerActivity : AppCompatActivity(), OrderHistoryAdapter.OnItemClickListener {

    private val TAG = "ViewCustomerActivity"
    lateinit var binding: ActivityViewCustomerBinding
    val viewModel: ViewCustomerVM by viewModels()
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
                            tvCustomerViewContact.text = this?.msPhoneNo
                            tvCustomerViewStreet.text  = this?.area ?: ""
                            tvCustomerViewFlatNo.text = this?.houseNo

                        }
                    }
                }
                EventStatus.ERROR -> TODO()
                EventStatus.EMPTY -> TODO()
            }
        }

        viewModel.getCustomerOrders(customerId).observe(this) { it ->
            when (it.eventStatus) {
                EventStatus.LOADING -> TODO()
                EventStatus.SUCCESS -> {
                    customerWithCustomerOrder = it.data!!
                    customerWithCustomerOrder.customerOrders.forEach { co->
                        Log.d(TAG, "loadDataFromVM: ${co.customerId}n ${co.orderId}")
                    }
                    val mAdapter = OrderHistoryAdapter(customerWithCustomerOrder.customerOrders)
                    binding.rViewRecentOrdersCustomer.apply {
                        layoutManager = LinearLayoutManager(applicationContext)
                        adapter = mAdapter
                    }

                    mAdapter.setOnItemClickListener(this)
                }
                EventStatus.ERROR -> TODO()
                EventStatus.EMPTY -> TODO()
            }
        }
    }

    private fun setToolbar() {
        findViewById<TextView>(R.id.tv_tbTitle).text = "Customer Profile"
        findViewById<ImageButton>(R.id.bt_backToolbar).setOnClickListener {
            finish()
        }
    }

    override fun onItemClick(position: Int, orderId : Long) {
        val intent = Intent(this, ViewOrderDetailsActivity::class.java)
        intent.putExtra(Constants.INTENT_DATA_CUSTOMER_ID, customerId)
        intent.putExtra(
            Constants.INTENT_DATA_ORDER_ID,
            orderId
        )
        startActivity(intent)
    }
}