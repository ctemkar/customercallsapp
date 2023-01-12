package com.smartshehar.customercallingv2.activities.customer.order.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.smartshehar.customercallingv2.databinding.ActivityAddCustomerOrderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCustomerOrderActivity : AppCompatActivity() {

    private val TAG = "AddCustomerOrderActivit"
    lateinit var binding: ActivityAddCustomerOrderBinding
    val viewModel: AddCustomerOrderVM by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}