package com.smartshehar.customercallingv2.activities.order.viewallorders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.activities.adapters.OrderHistoryAllAdapter
import com.smartshehar.customercallingv2.databinding.ActivityAllOrdersBinding
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllOrdersActivity : AppCompatActivity() {
    private val TAG = "AllOrdersActivity"
    lateinit var binding: ActivityAllOrdersBinding
    val viewModel: AllOrdersVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getAllCustomerOrders().observe(this) { it ->
            when (it.eventStatus) {
                EventStatus.LOADING -> TODO()
                EventStatus.SUCCESS -> {
                    val mAdapter = OrderHistoryAllAdapter(it.data!!)
                    binding.rViewAllOrders.apply {
                        layoutManager = LinearLayoutManager(applicationContext)
                        adapter = mAdapter
                    }

                    it.data!!.forEach { i->
                        Log.d(TAG, "onCreate: ${i.customer.firstName}")
                    }

                }
                EventStatus.ERROR -> TODO()
                EventStatus.EMPTY -> TODO()
            }
        }

        setToolbar()
    }

    private fun setToolbar() {
        findViewById<TextView>(R.id.tv_tbTitle).text = "All Orders"
        findViewById<ImageButton>(R.id.bt_backToolbar).setOnClickListener {
            finish()
        }
    }
}