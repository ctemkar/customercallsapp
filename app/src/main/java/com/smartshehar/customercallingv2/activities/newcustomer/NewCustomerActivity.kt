package com.smartshehar.customercallingv2.activities.newcustomer

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.smartshehar.customercallingv2.databinding.ActivityNewCustomerBinding
import com.smartshehar.customercallingv2.events.EventStatus
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.repositories.sqlite.dao.CustomerDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class NewCustomerActivity : AppCompatActivity() {

    private val TAG = "NewCustomerActivity"
    private lateinit var binding : ActivityNewCustomerBinding
    val viewModel : NewCustomerVM by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.etNewCustomerName.requestFocus()

        binding.btAddCustomer.setOnClickListener {
            if(validCustomer()) {
                val customer = Customer()
                customer.firstName = binding.etNewCustomerName.text.toString()
                customer.msPhoneNo = binding.etNewCustomerPhone.text.toString()
                viewModel.createNewCustomer(customer).observe(this){
                    when(it.eventStatus){
                        EventStatus.LOADING -> TODO()
                        EventStatus.SUCCESS -> {
                            finish()
                        }
                        EventStatus.ERROR -> TODO()
                        EventStatus.EMPTY -> TODO()
                    }
                }
            }
        }

    }

    private fun validCustomer(): Boolean {
        //Validations need to be done
        return true
    }
}
