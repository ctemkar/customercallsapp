package com.smartshehar.customercallingv2.activities.home

import android.Manifest.permission.READ_CALL_LOG
import android.Manifest.permission.READ_PHONE_STATE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.activities.adapters.CustomerListHomeAdapter
import com.smartshehar.customercallingv2.activities.menuitems.view.ViewMenuItemsActivity
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.activities.customer.addcustomer.AddCustomerActivity
import com.smartshehar.customercallingv2.activities.customer.view.ViewCustomerActivity
import com.smartshehar.customercallingv2.activities.order.viewallorders.AllOrdersActivity
import com.smartshehar.customercallingv2.databinding.ActivityHomeBinding
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.utils.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val TAG = "HomeActivity"
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeActivityVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermissions()
        loadData()

        binding.fabNewCustomer.setOnClickListener {
            startActivity(Intent(this, AddCustomerActivity::class.java))
        }

        binding.rlMenuHome.setOnClickListener {
            startActivity(Intent(this, ViewMenuItemsActivity::class.java))
        }

        binding.rlOrdersHome.setOnClickListener{
            startActivity(Intent(this,AllOrdersActivity::class.java))
        }


    }

    private fun loadData() {
        viewModel.getCustomersLiveData().observe(this) {
            when (it.eventStatus) {
                EventStatus.EMPTY -> {

                }
                EventStatus.LOADING -> TODO()
                EventStatus.SUCCESS -> {
                    showCustomerDataList(it)
                }
                EventStatus.ERROR -> TODO()
            }
        }
    }

    private fun showCustomerDataList(it: EventData<List<Customer>>?) {
        if (it != null) {
            val mAdapter = CustomerListHomeAdapter(it.data!!)

            //Set recycler list items of customers data
            binding.rViewCustomersHome.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = mAdapter
            }


            //Set listener on list item to launch into new activity
            mAdapter.setOnClickListener(object :
                CustomerListHomeAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val intent =
                        Intent(applicationContext, ViewCustomerActivity::class.java)
                    intent.putExtra(Constants.INTENT_DATA_CUSTOMER_ID, it.data!![position].customerId.toLong())
                    Log.d(TAG, "onItemClick: ${it.data!![position].customerId}")
                    startActivity(intent)
                }
            })
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this@HomeActivity, READ_PHONE_STATE) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@HomeActivity,
                    READ_PHONE_STATE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    arrayOf(READ_PHONE_STATE), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    arrayOf(READ_PHONE_STATE), 1
                )
            }
        }
        if (!Settings.canDrawOverlays(this)) {
            requestPermission()
        }

        if (ContextCompat.checkSelfPermission(this@HomeActivity, READ_CALL_LOG) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@HomeActivity,
                    READ_CALL_LOG
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    arrayOf(READ_CALL_LOG), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    arrayOf(READ_CALL_LOG), 1
                )
            }
        }
    }

    private fun showDialog(titleText: String, messageText: String) {
        with(AlertDialog.Builder(this)) {
            title = titleText
            setMessage(messageText)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        try {
            startActivityForResult(intent, 10)
        } catch (e: Exception) {
            showDialog(
                "ASD",
                "ASd"
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(this, "Permission Required for handle calls", Toast.LENGTH_SHORT)
                        .show()
                    checkPermissions()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}
