package com.smartshehar.customercallingv2.activities.addcustomer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.databinding.ActivityAddCustomerBinding
import com.smartshehar.customercallingv2.events.EventStatus
import com.smartshehar.customercallingv2.models.Customer
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddCustomerActivity : AppCompatActivity() {

    private val TAG = "NewCustomerActivity"
    private lateinit var binding : ActivityAddCustomerBinding
    val viewModel : AddCustomerVM by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
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

        findViewById<ImageButton>(R.id.bt_backToolbar).setOnClickListener {
            finish()
        }

    }

    private fun validCustomer(): Boolean {
        //Validations need to be done
        return true
    }
}
