package com.smartshehar.customercallingv2.activities.home

import android.Manifest.permission.*
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amaze.emanage.events.EventData
import com.google.android.material.card.MaterialCardView
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.activities.adapters.AvailableRestaurantAdapter
import com.smartshehar.customercallingv2.activities.adapters.CustomerListHomeAdapter
import com.smartshehar.customercallingv2.activities.auth.AuthenticationVM
import com.smartshehar.customercallingv2.activities.auth.LoginActivity
import com.smartshehar.customercallingv2.activities.customer.addcustomer.AddCustomerActivity
import com.smartshehar.customercallingv2.activities.customer.view.ViewCustomerActivity
import com.smartshehar.customercallingv2.activities.home.permissions.PermissionModel
import com.smartshehar.customercallingv2.activities.home.permissions.PermissionsAdapter
import com.smartshehar.customercallingv2.activities.menuitems.view.ViewMenuItemsActivity
import com.smartshehar.customercallingv2.activities.order.viewallorders.AllOrdersActivity
import com.smartshehar.customercallingv2.databinding.ActivityHomeBinding
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.Owner
import com.smartshehar.customercallingv2.models.Restaurant
import com.smartshehar.customercallingv2.utils.Constants
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.utils.states.AuthState
import com.smartshehar.customercallingv2.utils.states.RestaurantState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val TAG = "HomeActivity"
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeActivityVM by viewModels()
    private val authViewModel: AuthenticationVM by viewModels()
    private var customersList = ArrayList<Customer>()
    var selectedRestaurant: Restaurant? = null
    var availableRestaurants = ArrayList<Restaurant>()
    private var mRestaurantAdapter: AvailableRestaurantAdapter? = null


    @Inject
    lateinit var authState: AuthState

    @Inject
    lateinit var restaurantState: RestaurantState
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermissions()

        setViewModelObservers()

        //Set Search change listener
        setSearchTextChangeListener()

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

    private fun setViewModelObservers() {
        //Start loading data from server and cache
        loadOwnedRestaurantsFromServer()
        observeCustomerList()
        loadProfileDataFromServer()
        observeRestaurantUpdation()
    }

    private fun loadOwnedRestaurantsFromServer() {
        viewModel.getRestaurantsList().observe(this) {
            when (it.eventStatus) {
                EventStatus.LOADING -> TODO()
                EventStatus.SUCCESS -> {
                    availableRestaurants = it.data as ArrayList<Restaurant>
                    mRestaurantAdapter = AvailableRestaurantAdapter(availableRestaurants)
                }
                EventStatus.ERROR -> TODO()
                EventStatus.EMPTY -> TODO()
                EventStatus.CACHE_DATA -> TODO()
            }
        }
    }


    private fun observeCustomerList() {
        viewModel.getCustomersLiveData().observe(this) {
            when (it.eventStatus) {
                EventStatus.EMPTY -> {

                }
                EventStatus.LOADING -> {
                    showFullScreenLoadingLayout("loadCustomerDataFromCacheAndServer")
                }
                EventStatus.SUCCESS -> {
                    hideSyncingView()
                    hideFullScreenLoadingLayout()
                    showCustomerDataList(it)
                }
                EventStatus.ERROR -> {
                    hideFullScreenLoadingLayout()
                    hideSyncingView()
                }
                EventStatus.CACHE_DATA -> {
                    hideSyncingView()
                    showCustomerDataList(it)
                }
            }
        }
    }

    private fun loadProfileDataFromServer() {
        authViewModel.getProfileData().observe(this) {
            when (it.eventStatus) {
                EventStatus.LOADING -> {
                    TODO()
                }
                EventStatus.SUCCESS -> {
                    Log.d(TAG, "loadData: ${it.data!!.ownerName}")
                    if (it.data != null) {
                        setSelectedRestaurant(it)
                        setRestaurantChangeListeners()
                    }
                }
                EventStatus.ERROR -> {
                    if (it.error == "NETWORK") {
                        Toast.makeText(
                            applicationContext,
                            "Please check your network",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Session Expired, please login again",
                            Toast.LENGTH_SHORT
                        ).show()
                        authState.clearLoginState()
                        startActivity(Intent(applicationContext, LoginActivity::class.java))
                    }
                }
                EventStatus.EMPTY -> TODO()
                EventStatus.CACHE_DATA -> TODO()
            }
        }
    }

    private fun setSelectedRestaurant(it: EventData<Owner>) {
        if (it.data!!.selectedRestaurant == null) {
            binding.tvSelectedRestaurant.text = "No selected restaurant"
        } else {
            binding.tvSelectedRestaurant.text =
                it.data!!.selectedRestaurant!!.restaurantName
            selectedRestaurant = it.data!!.selectedRestaurant
            restaurantState.saveCurrentRestaurantId(selectedRestaurant!!._id)
        }
    }

    private fun hideSyncingView() {
        findViewById<LinearLayout>(R.id.ll_syncingLayout).visibility = View.GONE
    }


    private fun setRestaurantChangeListeners() {
        binding.cardSelectedRestaurant.setOnClickListener {
            setupSelectCurrentRestaurantPopup()
        }
    }

    lateinit var dialog: Dialog
    private fun setupSelectCurrentRestaurantPopup() {
        if (this::dialog.isInitialized) {
            if (dialog.isShowing)
                dialog.dismiss()
        }

        dialog = Dialog(this@HomeActivity)
        dialog.setContentView(R.layout.alert_select_restaurants)
        if (selectedRestaurant != null) {
            dialog.findViewById<TextView>(R.id.tv_restaurantNameSelectedAlert).text =
                selectedRestaurant!!.restaurantName
        } else {
            //Hide the selected item and show no available
            dialog.findViewById<LinearLayout>(R.id.ll_activeRestaurant).visibility = View.GONE
            dialog.findViewById<TextView>(R.id.tv_noActiveRestaurantAlert).visibility = View.VISIBLE
        }
        if (mRestaurantAdapter != null) {
            dialog.findViewById<ProgressBar>(R.id.pgBar_availableRestaurants).visibility = View.GONE
            dialog.findViewById<RecyclerView>(R.id.rView_availableRestaurants).apply {
                adapter = mRestaurantAdapter
                layoutManager = LinearLayoutManager(applicationContext)
            }
            mRestaurantAdapter!!.setOnItemClickListener(object :
                AvailableRestaurantAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    updateSelectedRestaurant(position)
                    dialog.dismiss()
                }
            })
        } else {
            dialog.findViewById<ProgressBar>(R.id.pgBar_availableRestaurants).visibility = View.GONE
        }

        // val dialogButton: Button = dialog.findViewById(com.smartshehar.customercallingv2.R.id.dialogButtonOK) as Button
        // if button is clicked, close the custom dialog
        // if button is clicked, close the custom dialog
//        dialogButton.setOnClickListener(object : DialogInterface.OnClickListener() {
//            fun onClick(v: View?) {
//                dialog.dismiss()
//                Toast.makeText(applicationContext, "Dismissed..!!", Toast.LENGTH_SHORT).show()
//            }
//        })
        dialog.show()
    }


    var lastSelectedRestaurantPosition = 0
    private fun updateSelectedRestaurant(position: Int) {
        val selectedId = availableRestaurants[position]._id
        Log.d(TAG, "updateSelectedRestaurant: ")
        viewModel.updateCurrentSelectedRestaurant(selectedId)
        lastSelectedRestaurantPosition = position
    }

    private fun observeRestaurantUpdation() {
        viewModel.getSelectedRestaurantUpdateLiveData().observe(this) {
            when (it.eventStatus) {
                EventStatus.LOADING -> {
                    showFullScreenLoadingLayout("observeRestaurantUpdatedChange")
                }
                EventStatus.SUCCESS -> {
                    hideFullScreenLoadingLayout()
                    selectedRestaurant = availableRestaurants[lastSelectedRestaurantPosition]
                    binding.tvSelectedRestaurant.text = selectedRestaurant!!.restaurantName
                    viewModel.refreshCustomerListData()
                }
                EventStatus.ERROR -> {
                    hideFullScreenLoadingLayout()
                    Toast.makeText(
                        applicationContext,
                        "Unable to update current selection",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                EventStatus.EMPTY -> TODO()
                EventStatus.CACHE_DATA -> TODO()
            }
        }
    }

    private fun hideFullScreenLoadingLayout() {
        Log.d(TAG, "hideFullScreenLoadingLayout: ")
        binding.llMainContentHome.visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.lottie_loading_utensils).visibility = View.GONE
    }

    private fun showFullScreenLoadingLayout(caller : String) {
        Log.d(TAG, "showFullScreenLoadingLayout: $caller ")
        binding.llMainContentHome.visibility= View.GONE
        findViewById<LinearLayout>(R.id.lottie_loading_utensils).visibility = View.VISIBLE

    }


    lateinit var mAdapter: CustomerListHomeAdapter
    private fun showCustomerDataList(it: EventData<List<Customer>>?) {
        if (it!!.data != null) {
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

        if (!Settings.canDrawOverlays(this)) {
            requestPermission()
        }

        val isPhoneStatePermission =
            ContextCompat.checkSelfPermission(this@HomeActivity, READ_PHONE_STATE) ==
                    PackageManager.PERMISSION_GRANTED
        val isCallLogPermission =
            ContextCompat.checkSelfPermission(this@HomeActivity, READ_CALL_LOG) ==
                    PackageManager.PERMISSION_GRANTED
        val isMakeCallPermission =
            ContextCompat.checkSelfPermission(this@HomeActivity, CALL_PHONE) ==
                    PackageManager.PERMISSION_GRANTED

        if (isCallLogPermission && isMakeCallPermission && isPhoneStatePermission) {
            //Hide permissions view
            findViewById<MaterialCardView>(R.id.card_permissions).visibility = View.GONE
            return
        }

        val permissionsList = kotlin.collections.ArrayList<PermissionModel>()
        permissionsList.add(
            PermissionModel(
                1, "Interrupt Incoming Call", isPhoneStatePermission,
                READ_PHONE_STATE
            )
        )

        permissionsList.add(
            PermissionModel(
                2, "Retrieve Missed Calls", isCallLogPermission,
                READ_CALL_LOG
            )
        )

        permissionsList.add(
            PermissionModel(
                3, "Make Phone Calls", isMakeCallPermission,
                CALL_PHONE
            )
        )

        val rViewPermissions = findViewById<RecyclerView>(R.id.rView_permissions)
        val mAdapter = PermissionsAdapter(permissionsList)
        rViewPermissions.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = mAdapter
        }
        mAdapter.setOnItemClickListener(object : PermissionsAdapter.OnItemClickListener {
            override fun onItemClick(selectedPermission: PermissionModel) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@HomeActivity,
                        selectedPermission.permissionString
                    )
                ) {
                    ActivityCompat.requestPermissions(
                        this@HomeActivity,
                        arrayOf(selectedPermission.permissionString), 1
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this@HomeActivity,
                        arrayOf(selectedPermission.permissionString), 1
                    )
                }
            }
        })
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
                "Error",
                "Something happened! Try again"
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
                checkPermissions()
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
