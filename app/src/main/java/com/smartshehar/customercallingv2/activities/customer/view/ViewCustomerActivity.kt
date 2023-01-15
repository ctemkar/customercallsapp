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
import com.smartshehar.customercallingv2.databinding.ActivityViewCustomerBinding
import com.smartshehar.customercallingv2.utils.Constants
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewCustomerActivity : AppCompatActivity() {

    private val TAG = "ViewCustomerActivity"
    lateinit var binding: ActivityViewCustomerBinding
    val viewModel: ViewCustomerVM by viewModels()
    var customerId: Long = 0


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
                        }
                    }

                }
                EventStatus.ERROR -> TODO()
                EventStatus.EMPTY -> TODO()
            }
        }

        viewModel.getCustomerOrders(customerId).observe(this) {
            when (it.eventStatus) {
                EventStatus.LOADING -> TODO()
                EventStatus.SUCCESS -> {
                    val mAdapter = OrderHistoryAdapter(it.data!!)
                    binding.rViewRecentOrdersCustomer.apply {
                        layoutManager = LinearLayoutManager(applicationContext)
                        adapter = mAdapter
                    }
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
}