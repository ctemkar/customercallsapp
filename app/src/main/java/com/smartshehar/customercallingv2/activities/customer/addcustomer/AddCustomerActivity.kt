package com.smartshehar.customercallingv2.activities.customer.addcustomer

import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.databinding.ActivityAddCustomerBinding
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.utils.events.EventStatus
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
        setToolbar()
        binding.etNewCustomerName.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        binding.btAddCustomer.setOnClickListener {
            if(validCustomer()) {
                val customer = Customer()
                customer.firstName = binding.etNewCustomerName.text.toString()
                customer.contactNumber = binding.etNewCustomerPhone.text.toString()
                customer.houseNo = binding.etNewCustomerHouseFlatNo.text.toString()
                customer.area = binding.etNewCustomerAreaStreet.text.toString()
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
