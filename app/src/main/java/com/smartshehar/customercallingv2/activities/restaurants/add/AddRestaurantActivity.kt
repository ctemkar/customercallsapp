package com.smartshehar.customercallingv2.activities.restaurants.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.databinding.ActivityAddRestaurantBinding
import com.smartshehar.customercallingv2.models.dtos.CreateRestaurantRq
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddRestaurantActivity : AppCompatActivity() {
    private val TAG = "AddRestaurantActivity"
    lateinit var binding: ActivityAddRestaurantBinding
    val viewModel: AddRestaurantVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
        setLiveDataListeners()
        binding.btAddRestaurant.setOnClickListener {
            validateAndCreateRestaurant()
        }


    }

    private fun setLiveDataListeners() {
        viewModel.getCreateRestaurantLiveData().observe(this) {
            hideFullScreenLoadingLayout()

            when (it.eventStatus) {
                EventStatus.SUCCESS -> {
                    finish()
                }
                EventStatus.ERROR -> {
                    Log.d(TAG, "setLiveDataListeners: Err")
                }
                else -> {

                }
            }
        }
    }

    private fun validateAndCreateRestaurant() {
        val createRestaurantRq =
            CreateRestaurantRq(
                restaurantName = binding.etNewRestaurantName.text.toString(),
                contactNumber = binding.etNewRestaurantContact.text.toString(),
                addressLine1 = binding.etNewRestaurantAddressLine1.text.toString(),
                addressLine2 = binding.etNewRestaurantAddressLine2.text.toString(),
                pincode = 0
            )

        if (!validateInput(createRestaurantRq)) {
            return
        }
        createRestaurantRq.pincode = binding.etNewRestaurantPincode.text.toString().toLong()
        showFullScreenLoadingLayout()
        viewModel.createRestaurant(createRestaurantRq)
    }

    private fun validateInput(createRestaurantRq: CreateRestaurantRq): Boolean {
        val pincode = binding.etNewRestaurantPincode.text.toString()

        if (createRestaurantRq.restaurantName.isBlank()) {
            binding.etNewRestaurantName.error = "Name required"
            binding.etNewRestaurantName.requestFocus()
        } else if (createRestaurantRq.contactNumber.isBlank()) {
            binding.etNewRestaurantContact.error = "Contact required"
            binding.etNewRestaurantContact.requestFocus()
        } else if (createRestaurantRq.addressLine1.isBlank()) {
            binding.etNewRestaurantAddressLine1.error = "Invalid Address"
            binding.etNewRestaurantAddressLine1.requestFocus()
        } else if (createRestaurantRq.addressLine2.isBlank()) {
            binding.etNewRestaurantAddressLine2.error = "Invalid Address"
            binding.etNewRestaurantAddressLine2.requestFocus()
        } else if (pincode.isBlank()) {
            binding.etNewRestaurantPincode.error = "Invalid pincode"
            binding.etNewRestaurantPincode.requestFocus()
        } else {
            return true
        }
        return false
    }

    private fun hideFullScreenLoadingLayout() {
        binding.slContentAddRestaurant.visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.lottie_loading_utensils).visibility = View.GONE
    }

    private fun showFullScreenLoadingLayout() {
        binding.slContentAddRestaurant.visibility = View.GONE
        findViewById<LinearLayout>(R.id.lottie_loading_utensils).visibility = View.VISIBLE

    }

    private fun setToolbar() {
        findViewById<TextView>(R.id.tv_tbTitle).text = "New Restaurant"
        findViewById<ImageButton>(R.id.bt_backToolbar).setOnClickListener {
            finish()
        }
    }
}