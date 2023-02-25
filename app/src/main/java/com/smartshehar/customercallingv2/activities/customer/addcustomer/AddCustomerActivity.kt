package com.smartshehar.customercallingv2.activities.customer.addcustomer

import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.databinding.ActivityAddCustomerBinding
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.utils.Constants.Companion.NETWORK_ERROR
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddCustomerActivity : AppCompatActivity() {

    private val TAG = "NewCustomerActivity"
    private lateinit var binding: ActivityAddCustomerBinding
    val viewModel: AddCustomerVM by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
        binding.etNewCustomerName.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        binding.btAddCustomer.setOnClickListener {
            if (validCustomer()) {
                val customer = Customer()
                customer.firstName = binding.etNewCustomerName.text.toString()
                customer.contactNumber = binding.etNewCustomerPhone.text.toString()
                customer.houseNo = binding.etNewCustomerHouseFlatNo.text.toString()
                customer.addressLine1 = binding.etNewCustomerAddressLine1.text.toString()
                customer.pincode = binding.etNewCustomerPincode.text.toString().toLong()
                viewModel.createNewCustomer(customer).observe(this) {
                    when (it.eventStatus) {
                        EventStatus.LOADING -> TODO()
                        EventStatus.SUCCESS -> {
                            finish()
                        }
                        EventStatus.ERROR -> {
                            if (it.error == NETWORK_ERROR) {
                                Toast.makeText(
                                    applicationContext,
                                    "Unable to sync, will sync once network is stable",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                        EventStatus.EMPTY -> TODO()
                        EventStatus.CACHE_DATA -> {

                        }
                    }
                }
            }
        }


    }


    private fun setToolbar() {
        findViewById<TextView>(R.id.tv_tbTitle).text = "New Customer"
        findViewById<ImageButton>(R.id.bt_backToolbar).setOnClickListener {
            finish()
        }
    }

    private fun validCustomer(): Boolean {
        //Validations need to be done
        return true
    }
}
