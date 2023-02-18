package com.smartshehar.customercallingv2.activities.home

import android.Manifest.permission.*
import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.amaze.emanage.events.EventData
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.smartshehar.customercallingv2.activities.adapters.CustomerListHomeAdapter
import com.smartshehar.customercallingv2.activities.auth.AuthenticationVM
import com.smartshehar.customercallingv2.activities.auth.LoginActivity
import com.smartshehar.customercallingv2.activities.customer.addcustomer.AddCustomerActivity
import com.smartshehar.customercallingv2.activities.customer.view.ViewCustomerActivity
import com.smartshehar.customercallingv2.activities.menuitems.view.ViewMenuItemsActivity
import com.smartshehar.customercallingv2.activities.order.viewallorders.AllOrdersActivity
import com.smartshehar.customercallingv2.databinding.ActivityHomeBinding
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.utils.Constants
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.utils.states.AuthState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val TAG = "HomeActivity"
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeActivityVM by viewModels()
    private val authViewModel : AuthenticationVM by viewModels()
    private var customersList = ArrayList<Customer>()
    @Inject
    lateinit var authState : AuthState
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

        binding.rlOrdersHome.setOnClickListener {
            startActivity(Intent(this, AllOrdersActivity::class.java))
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

        authViewModel.getProfileData().observe(this){
            when(it.eventStatus){
                EventStatus.LOADING -> {
                    TODO()
                }
                EventStatus.SUCCESS -> {
                    Log.d(TAG, "loadData: ${it.data!!.ownerName}")
                    if(it.data != null){
                        if(it.data!!.selectedRestaurant == null){
                            binding.tvSelectedRestaurant.text = "No selected restaurant"
                        }else{
                            binding.tvSelectedRestaurant.text = it.data!!.selectedRestaurant!!.restaurantName
                        }
                        Toast.makeText(applicationContext,it.data!!.ownerName,Toast.LENGTH_SHORT).show()
                        powerMenu.showAsDropDown(binding.cardSelectedRestaurant)
                    }
                }
                EventStatus.ERROR -> {
                    Toast.makeText(applicationContext,"Session Expired, please login again",Toast.LENGTH_SHORT).show()
                    authState.clearLoginState()
                    startActivity(Intent(applicationContext,LoginActivity::class.java))
                }
                EventStatus.EMPTY -> TODO()
            }
        }

        setupMenu()

    }

    lateinit var powerMenu: PowerMenu
    fun setupMenu(){
        val onMenuItemClickListener: OnMenuItemClickListener<PowerMenuItem?> =
            OnMenuItemClickListener<PowerMenuItem?> { position, item ->
                //Toast.makeText(baseContext, item.getTitle(), Toast.LENGTH_SHORT).show()
                powerMenu.selectedPosition = position // change selected item
                powerMenu.dismiss()
            }
        powerMenu = PowerMenu.Builder(applicationContext)
            // list has "Novel", "Poetry", "Art"
            .addItem(PowerMenuItem("Journals", false)) // add an item.
            .addItem(PowerMenuItem("Travel", false)) // aad an item list.
            .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
            .setMenuRadius(10f) // sets the corner radius.
            .setMenuShadow(10f) // sets the shadow.
            .setTextColor(ContextCompat.getColor(applicationContext, R.color.holo_red_dark))
            .setTextGravity(Gravity.CENTER)
            .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
            .setSelectedTextColor(Color.WHITE)
            .setMenuColor(Color.WHITE)
            .setSelectedMenuColor(ContextCompat.getColor(applicationContext, R.color.holo_purple))
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .build()


    }

    lateinit var mAdapter: CustomerListHomeAdapter
    private fun showCustomerDataList(it: EventData<List<Customer>>?) {
        if (it != null) {
            customersList = (it.data as ArrayList<Customer>?)!!
            mAdapter = CustomerListHomeAdapter(customersList)

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
                    intent.putExtra(
                        Constants.INTENT_DATA_CUSTOMER_ID,
                        it.data!![position].customerId.toLong()
                    )
                    Log.d(TAG, "onItemClick: ${it.data!![position].customerId}")
                    startActivity(intent)
                }
            })

            setSearchTextChangeListener()
        }
    }

    private fun setSearchTextChangeListener() {
        binding.etSearchCustomersHome.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                filterSearchResults(s)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

    }

    private fun filterSearchResults(searchText: CharSequence) {
        if (searchText.isEmpty()) {
            mAdapter.updateData(customersList)
        } else {
            val customersSearchList = ArrayList<Customer>()
            customersList.forEach {
                if (it.firstName?.contains(searchText) == true) {
                    customersSearchList.add(it)
                } else if (it.contactNumber.contains(searchText)) {
                    customersSearchList.add(it)
                }
            }
            mAdapter.updateData(customersSearchList)
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

        if (ContextCompat.checkSelfPermission(this@HomeActivity, CALL_PHONE) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@HomeActivity,
                    CALL_PHONE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    arrayOf(CALL_PHONE), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    arrayOf(CALL_PHONE), 1
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
